package controllers.system

import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

import akka.actor.ActorSystem
import models.{Article, Menu, TreeNode}
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.{Configuration, Logger}
import reactivemongo.bson.BSONDocument
import services.MenuService
import utils.{Common, FileUtil}

import scala.concurrent.{ExecutionContext, Future}

class MenuController  @Inject()(
                                 config: Configuration,
                                 components: ControllerComponents,
                                 menuService: MenuService,
                                 system: ActorSystem
                               ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {

  final val IMG_PATH = System.getProperty("user.dir") + config.underlying.getString("play.assets.path") + File.separator + "images"

  /**
    * 上传测试
    * @return
    */
  def upload = Action.async(parse.multipartFormData) { implicit request =>
    try {
      for (file <- request.body.files) {
        val filename = System.currentTimeMillis() + "_" + file.filename
        file.ref.moveTo(Paths.get(IMG_PATH + File.separator + filename), replace = true)
      }
      println(s"move over!")
      Future.successful(Ok(Json.obj("msg" -> "upload completed!")))
    } catch  {
      case e: Exception =>
        e.printStackTrace()
        Future.successful(BadRequest(Json.obj("error" -> "upload error")))
    }
  }

  def addBannerImages(menu: Option[Int]) = Action.async(parse.multipartFormData) {
    implicit request =>
      try {
        val fileNames = for (file <- request.body.files) yield {
          val filename = System.currentTimeMillis() + "_" + file.filename
          file.ref.moveTo(Paths.get(IMG_PATH + File.separator + filename), replace = true)
          filename
        }
        menuService.addImages(menu.get, fileNames)
        println(s"move over! menu: $menu")
        Future.successful(Ok(Json.obj("msg" -> "upload completed!")))
      } catch  {
        case e: Exception =>
          e.printStackTrace()
          Future.successful(BadRequest(Json.obj("error" -> "upload error")))
      }
  }

  /**
    * 显示菜单
    * @return
    */
  def listMenus = Action.async { implicit request =>
    menuService.queryMenus().map{ list =>
      val tree = makeTree(list, 0).sortBy(_.order)
      tree.foreach(_.sort())
//      val jsValue = tree.map(x => Json.toJson(x))
      Ok(Json.toJson(tree))
    }
  }

  def makeTree(list: Seq[Menu], root: Int): Seq[TreeNode] = {
    val nodes = list.filter(x => x.parent == root).map { x =>
      TreeNode(x.id, x.name, x.order, x.desc, Nil)
    }
    nodes.foreach { n =>
      makeTree(list, n.id).foreach(n.addChild)
    }
    nodes
  }


  /**
    * 删除图片
    * @return
    */
  def deleteImage() = Action.async(parse.json) { implicit request =>
    val menu = (request.body \ "menu").as[Int]
    val index = (request.body \ "index").as[String]
    menuService.deleteImage(menu, index, IMG_PATH + File.separator).map{result =>
      Ok(Json.obj("banner" -> result.banner))
    }.recover{
      case e: Exception => BadRequest(Json.obj("error" -> e.getMessage))
    }
  }

  /**
    * 修改图片
    * 前端生成图片链接 带menu和index参数，或者 前端路由时传入
    * 1.上传图片
    * 2.删除旧图
    * 3.替换旧图与菜单关联
    * @param menu
    * @param index
    * @return
    */
  def editImage(menu: Int, index: String) = Action.async(parse.multipartFormData) { implicit request =>
    try {
      for (file <- request.body.files) {
        val filename = System.currentTimeMillis() + "_" + file.filename
        val newFilename = IMG_PATH + File.separator + filename
        file.ref.moveTo(Paths.get(newFilename), replace = true)
        Logger.debug(s"移动文件完毕: $newFilename")

        menuService.getImageName(menu, index).foreach {
          case Some(img) =>
            val file = new File(IMG_PATH + File.separator + img)
            FileUtil.deleteFile(file)
          case None =>
            Logger.debug("没有文件可以移动")
        }
        menuService.replaceImage(menu, index, newFilename)
      }
      Future.successful(Ok(Json.obj("msg" -> "upload completed!")))
    } catch  {
      case e: Exception =>
        e.printStackTrace()
        Future.successful(BadRequest(Json.obj("error" -> "upload error")))
    }
  }

  /**
    * 改变图片顺序
    * @param menu
    * @param index
    * @param changeTo
    * @return
    */
  def changeImageOrder(menu: Int, index: String, changeTo: String) =
    Action.async { implicit request =>
      menuService.findMenu(menu).flatMap{
        case Some(m) =>
          var banner = m.banner
          val from = m.banner(index)
          m.banner.get(changeTo) match {
            case Some(v) => banner += index -> v
            case None => banner -= index
          }
          menuService.updateMenu(m.copy(banner = banner.updated(changeTo, from))).map(_ =>
            Ok(Json.obj("info" -> "changeCompleted")))
        case None => Future(BadRequest(Json.obj("error" -> s"menu $menu not exists")))
      }
  }

  /**
    * 列出指定菜单的子栏目
    * @param menu
    * @return
    */
  def findChildrenOfMenu(menu: Int) = Action.async { implicit request =>
    menuService.findChildrenOfMenu(menu).map { list =>
      val tree = makeTree(list, menu).sortBy(_.order)
      tree.foreach(_.sort())
      val jsValue = tree.map(x => Json.toJson(x))
      Ok(JsArray(jsValue))
      //      Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.name)))
    }
  }

  /**
    * 修改菜单排序
    * @param menu
    * @param changeTo
    * @return
    */
  def changeMenuOrder(menu: Int, changeTo: String) = Action.async {
    implicit request =>
      menuService.findMenu(menu).flatMap {
        case Some(menu1) =>
          val f = menuService.findMenuByOrder(menu1.parent, changeTo.toInt).map {
            case Some(m2) => List(m2.copy(order = menu1.order), menu1.copy(order = changeTo.toInt))
            case None => List(menu1.copy(order = changeTo.toInt))
          }
          f.flatMap{ m =>
            Future.sequence(m.map{ e =>
              menuService.updateMenu(e)
            }).map(x =>

              Ok(JsArray(x.map(v => Json.obj("menu" -> v.id, "order" -> v.order)))))
          }
        case None =>
          Future(BadRequest(Json.obj("error" -> "wrong menu")))
      }
  }

  /**
    * 添加菜单
    * @return
    */
  def addMenu() = Action.async(parse.json) {
    implicit request =>
      val id = menuService.MENU_AUTO_ID.addAndGet(1)
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      menuService.addMenu(jsObject).map(m => Ok(Json.toJsObject(m)))
  }

  /**
    * 删除菜单
    * @param menu
    * @return
    */
  def removeMenu(menu: Int) = Action.async {
    implicit request =>
      menuService.findMenu(menu).flatMap{
        case Some(m) => menuService.removeMenu(menu).map(_ => Ok(Json.obj("info" -> "delete complete")))
        case None => Future(BadRequest(Json.obj("error" -> "nothing to delete")))
      }
  }

  /**
    * 编辑菜单
    * @param menu
    * @return
    */
  def editMenu(menu: Int) = Action.async(parse.json) {
    implicit request =>
      menuService.findMenu(menu).flatMap{
        case Some(m) => //Tip:  withModifier也可返回Menu.copy()对象
          var modifier = BSONDocument.empty
          (request.body \ "parent").asOpt[Int].foreach{ v =>
            modifier ++= ("parent" -> v)
          }
          (request.body \ "name").asOpt[String].foreach{ v =>
            modifier ++= ("name" -> v)
          }
          (request.body \ "desc").asOpt[String].foreach{ v =>
            modifier ++= ("desc" -> v)
          }
          (request.body \ "content").asOpt[String].foreach{ v =>
            modifier ++= ("content" -> v)
          }
          (request.body \ "order").asOpt[Int].foreach{ v =>
            modifier ++= ("order" -> v)
          }
          (request.body \ "banner").asOpt[JsObject].foreach{ v =>
            var bson = BSONDocument.empty
            v.value foreach { case (k, v) =>
              bson ++= (k -> v.toString())
            }
            modifier ++= ("banner" -> bson)
          }
          menuService.updateMenu(BSONDocument("id" -> menu), modifier).map(_ => Ok(Json.obj("info" -> "ok")))
        case None => Future(BadRequest(Json.obj("error" -> "wrong menu")))
      }
  }

  /**
    * 获取首页banner
    * @return
    */
  def homeBanners() = Action.async{
    implicit request =>
      menuService.findMenu(1).map {
        case Some(m) => Ok(Json.obj("banners" -> m.banner.values))
        case None => BadRequest(Json.obj("error" -> "cant find menu"))
      }
  }

  /**
    * 菜单栏目明细
    * @param menu
    * @return
    */
  def showMenu(menu: Option[Int]) = Action.async{
    implicit request =>
      menu match {
        case Some(id) =>
          menuService.findMenu(id).map{
            case Some(m) => Ok(Json.obj("menu" -> Json.toJsObject(m)))
            case None => BadRequest(Json.obj("error" -> "wrong menu"))
          }
        case None =>
          Future(Ok(Json.obj("info" -> "empty")))
      }
  }
}
