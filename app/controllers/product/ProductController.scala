package controllers.product

import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

import akka.actor.ActorSystem
import models.{Category, Department, Product}
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import reactivemongo.bson.BSONDocument
import services.ProductService

import scala.concurrent.{ExecutionContext, Future}

class ProductController @Inject()(
                                  config: Configuration,
                                  components: ControllerComponents,
                                  productService: ProductService,
                                  system: ActorSystem
                                ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport  {

  final val IMG_PATH = System.getProperty("user.dir") + config.underlying.getString("play.assets.path") + File.separator + "images"

  //添加产品
  def addProduct() = Action.async(parse.json) {
    implicit request =>
          val jValue = request.body.as[JsObject]
          val attributes = (jValue \ "attributes").as[JsArray].value map { v =>
            val key = (v \ "key").as[String]
            val value = (v \ "value").as[String]
            Map("key" -> key,"value" -> value)
          }
          val id = productService.PRODUCT_AUTO_ID.addAndGet(1)
          val m11 = (jValue \ "images").get.asInstanceOf[JsObject].value
          val m22 = m11.map { case (kk, vv) =>
            (kk, vv.toString())
          }

          val product = Product(id,
            (jValue \ "name").as[String],
            (jValue \ "sku").as[String],
            (jValue \ "category").as[Int],
            (jValue \ "parent").as[Int],
            attributes.toArray,
            (jValue \ "content").as[String],
            (jValue \ "price").as[Double],
            (jValue \ "sellPrice").as[Double],
            (jValue \ "asin").as[String],
            (jValue \ "stock").as[Int],
            (jValue \ "show").as[Boolean],
            m22.toMap,
            (jValue \ "link").as[String]
          )

          productService.addProduct(product).flatMap{ s =>
            Future(Ok(Json.obj("ok" -> "ok")))
          }
  }
  //删除产品
  def removeProduct(product: Int) = Action.async {
    implicit request =>
      productService.removeProduct(product).map { case Some(s) =>
        Ok(Json.obj("info" -> "delete completed!"))
      }
  }

  //编辑产品
  def editProduct() = Action.async(parse.json) {
    implicit  request =>
      val s = request.body.as[Product]
      productService.updateProduct(BSONDocument("id" -> s.id), s).flatMap { case Some(sk) =>
        Future(Ok(Json.obj("info" -> "ok")))
      }
  }

  //查找产品
  def findProduct(product: Option[Int]) = Action.async {
    implicit request =>
      product match {
        case Some(id) =>
          productService.findProduct(id).map{
            case Some(s) => Ok(Json.obj("product" -> Json.toJsObject(s)))
            case None => BadRequest(Json.obj("error" -> "wrong product"))
          }
        case _ =>
          Future(Ok(Json.obj("info" -> "empty")))
      }

  }

  def findProductsByParent(parent: Int) = Action.async {
    implicit request =>
      productService.findProductsByParent(parent).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.name)))
      }
  }
  //sku列表
  def listProducts() = Action.async {
    implicit request =>
      productService.queryProducts().map{ list =>
        Ok(JsArray(list.map(x => Json.toJson(x))))
      }
  }
  // 添加产品类型
  def addCategory() = Action.async(parse.json) {
    implicit request =>
      val id = productService.CATEGORY_AUTO_ID.incrementAndGet()
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      productService.addCategory(jsObject.as[Category]).map(m => Ok(Json.toJsObject(m)))
  }

  def addDepartment() = Action.async(parse.json) {
    implicit request =>
      val id = productService.DEPARTMENT_AUTO_ID.incrementAndGet()
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      productService.addDepartment(jsObject.as[Department]).map(m => Ok(Json.toJsObject(m)))
  }

  //删除产品类型
  def removeCategory(category: Int) = Action.async {
    implicit request =>
      productService.removeCategory(category).map {
        case Some(c) => Ok(Json.obj("info" -> s"delete ${c.name} complete"))
      }
  }

  //修改产品类型
  def editCategory() = Action.async(parse.json) {
    implicit request =>
      val c = request.body.as[Category]
      productService.updateCategory(BSONDocument("id" -> c.id), c).flatMap { case Some(cate) =>
        productService.updateProductCategory(c.id, cate.id).map(_ =>
          Ok(Json.obj("info" -> s"edit ${c.name} ok"))
        )
      }
     /* productService.findCategory(category).flatMap{
        case Some(c) =>
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
          productService.updateCategory(BSONDocument("id" -> category), modifier).map(_ => Ok(Json.obj("info" -> "ok")))
        case None =>
          Future(BadRequest(Json.obj("error" -> "wrong category")))
      }*/
  }

  def addCategoryBanner(category: Option[Int]) = Action.async(parse.multipartFormData) {
    implicit request =>
      try {
        (for{
              cate <- category
              file <- request.body.files.headOption
            } yield {
            val filename = System.currentTimeMillis() + "_" + file.filename
            file.ref.moveTo(Paths.get(IMG_PATH + File.separator + filename), replace = true)
            productService.updateCategoryBanner(cate, filename)
            Future.successful(Ok(Json.obj("msg" -> "upload completed!")))
        }).getOrElse(Future(BadRequest("no file to upload")))
      } catch  {
        case e: Exception =>
          e.printStackTrace()
          Future.successful(BadRequest(Json.obj("error" -> "upload error")))
      }
  }


  //产品类型列表
  def listCategories() = Action.async {
    implicit request =>
      productService.queryCategories().map{ list =>
        Ok(JsArray(list.map(x => Json.toJsObject(x))))
      }
  }

  def listDepartments() = Action.async {
    implicit request =>
      productService.queryDepartments().map{ list =>
        Ok(JsArray(list.map(x => Json.toJsObject(x))))
      }
  }

  def listTopCategories() = Action.async {
    implicit request =>
      productService.queryTopCategories().map { list =>
        Ok(JsArray(list.map(x => Json.toJsObject(x))))
      }
  }

  //查找产品类型
  def findCategory(category: Option[Int]) = Action.async {
    implicit request =>
      category match {
        case Some(id) =>
          productService.findCategory(id).map{
            case Some(c) => Ok(Json.obj("category" -> Json.toJsObject(c)))
            case None => BadRequest(Json.obj("error" -> "wrong menu"))
          }
        case None =>
          Future(Ok(Json.obj("info" -> "empty")))
      }
  }

  //查找产品类型
  def findDepartment(department: Option[Int]) = Action.async {
    implicit request =>
      department match {
        case Some(id) =>
          productService.findDepartment(id).map{
            case Some(c) => Ok(Json.obj("department" -> Json.toJsObject(c)))
            case None => BadRequest(Json.obj("error" -> "wrong department"))
          }
        case None =>
          Future(Ok(Json.obj("info" -> "empty")))
      }
  }
  //评论列表
  def listComments() = Action.async {
    implicit request =>
      productService.queryComments().map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.title)))
      }
  }
  //查找评论
  def commentDetail(comment: Int) = Action.async {
    implicit request =>
      productService.findComment(comment).map{
        case Some(c) => Ok(Json.toJsObject(c))
        case None => BadRequest(Json.obj("error" -> "wrong menu"))
      }
  }

  //查找评论 根据产品和型号
  def findComment(product: Option[Int]) = Action.async {
    implicit request =>
      productService.findComment(product).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.title)))
      }
  }
}
