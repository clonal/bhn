package controllers.system

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import reactivemongo.bson.BSONDocument
import services.{ArticleService, MenuService}

import scala.concurrent.{ExecutionContext, Future}

class ArticleController @Inject()(
                                   config: Configuration,
                                   components: ControllerComponents,
                                   articleService: ArticleService,
                                   menuService: MenuService,
                                   system: ActorSystem
                                 ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {

  /**
    * 显示文章
    * @param article
    * @return
    */
  def showArticle(article: Option[Int]) = Action.async {
    implicit request =>
      article match {
        case Some(id) =>
          articleService.findArticle(id).map{
            case Some(a) => Ok(Json.obj("article" -> Json.toJsObject(a)))
            case None => BadRequest(Json.obj("error" -> "wrong article"))
          }
        case _ =>
          Future(Ok(Json.obj("info" -> "empty")))
      }
  }

  //列出文章(含过滤）
  def listArticles(menu: Int, asc: Option[Int], recursive: Option[Boolean]) = Action.async {
    implicit request =>
      val _asc = asc.getOrElse(-1)
      val rec = recursive.getOrElse(false)
      val result = if (rec) {
          menuService.findChildrenOfMenu(menu).flatMap { menus =>
            val r = menus.map { menu =>
              articleService.queryArticles(menu.id, _asc)
            }
            Future.sequence(r).flatMap{ x =>
              articleService.queryArticles(menu, _asc).map { list =>
                list ++ x.flatten
              }
            }
          }
      } else {
        articleService.queryArticles(menu, _asc)
      }
      result.map{ list =>
        Ok(Json.toJson(list))
        //        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.title)))
      }
  }

  //编辑文章
  def editArticle(article: Int) = Action.async(parse.json) {
    implicit request =>
      articleService.findArticle(article).flatMap{
        case Some(a) =>
          var modifier = BSONDocument.empty
          (request.body \ "menu").asOpt[Int].foreach{ v =>
            modifier ++= ("menu" -> v)
          }
          (request.body \ "title").asOpt[String].foreach{ v =>
            modifier ++= ("title" -> v)
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
          articleService.updateArticle(BSONDocument("id" -> article), modifier).map(_ => Ok(Json.obj("info" -> "ok")))
        case None => Future(BadRequest(Json.obj("error" -> "wrong article")))
      }
  }

  //删除文章
  def removeArticle(article: Int) = Action.async {
    implicit request =>
      articleService.findArticle(article).flatMap{
        case Some(m) => articleService.removeArticle(article).map(_ => Ok(Json.obj("info" -> "delete complete")))
        case None => Future(BadRequest(Json.obj("error" -> "nothing to delete")))
      }
  }

  //排序文章
  def changeArticleOrder(article: Int, changeTo: String) = Action.async {
    implicit request =>
      articleService.findArticle(article).flatMap {
        case Some(article1) =>
          val f = articleService.findArticleByOrder(article1.menu, changeTo.toInt).map {
            case Some(article2) => List(article2.copy(order = article1.order), article1.copy(order = changeTo.toInt))
            case None => List(article1.copy(order = changeTo.toInt))
          }
          f.flatMap{ m =>
            Future.sequence(m.map{ e =>
              articleService.updateArticle(e)
            }).map(x =>
              Ok(JsArray(x.map(v => Json.obj("article" -> v.id, "order" -> v.order)))))
          }
        case None =>
          Future(BadRequest(Json.obj("error" -> "wrong article")))
      }
  }

  /**
    * 新增文章
    * @return
    */
  def addArticle() = Action.async(parse.json) {
    implicit request =>
      val id = articleService.ARTICLE_AUTO_ID.addAndGet(1)
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
//      articleService.addArticle(jsObject).map(a => Ok(Json.toJsObject(a)))
      articleService.addArticle(jsObject).map(a => Ok(Json.obj("info" -> "success")))
  }

  //草稿箱 暂时不做
}
