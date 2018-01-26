package controllers.system

import javax.inject.Inject

import akka.actor.ActorSystem
import models.Article
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import reactivemongo.bson.BSONDocument
import services.{ArticleService, ColumnService}

import scala.concurrent.{ExecutionContext, Future}

class ArticleController @Inject()(
                                   config: Configuration,
                                   components: ControllerComponents,
                                   articleService: ArticleService,
                                   columnService: ColumnService,
                                   system: ActorSystem
                                 ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {

  /**
    * 显示文章
    * @param article
    * @return
    */
  def showArticle(article: Int) = Action.async {
    implicit request =>
        articleService.findArticle(article).map{
          case Some(a) => Ok(Json.obj("article" -> Json.toJsObject(a)))
          case None => BadRequest(Json.obj("error" -> "wrong article"))
        }
  }

  def listArticlesByColumn(column: Option[Int]): Future[Seq[Article]] = {
    column match {
      case Some(col) =>
        columnService.fetchColumn(col) match {
          case Some(_) =>
            val children = columnService.fetchChildrenColumn(col)
            if (children.nonEmpty) {
              val futures = children.foldLeft(Seq(Future(Seq.empty[Article])))((a, b) =>
                a :+ listArticlesByColumn(Some(b.id)))
              Future.sequence(futures).map(_.flatten)
              /*              children.foldLeft(Future(Seq.empty[Article]))((future, element) =>
                              f(element.id).flatMap(seq => future.map(_ ++ seq)))*/
            } else {
              articleService.queryArticles(col, -1)
            }
          case None =>
            Future(Seq.empty[Article])
        }
      case None =>
        articleService.queryAllArticles()
    }
  }

  def listArticles(column: Option[Int]) = Action.async {
    implicit request =>
      listArticlesByColumn(column).map{ list =>
        Ok(Json.toJson(list))
      }
  }

  //列出文章(含过滤）
/*  def listArticles(menu: Int, asc: Option[Int], recursive: Option[Boolean]) = Action.async {
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
      }
  }*/

  //编辑文章
  def editArticle(article: Int) = Action.async(parse.json) {
    implicit request =>
      articleService.findArticle(article).flatMap{
        case Some(a) =>
          var modifier = BSONDocument.empty
          (request.body \ "column").asOpt[Int].foreach{ v =>
            modifier ++= ("column" -> v)
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
          val f = articleService.findArticleByOrder(article1.column, changeTo.toInt).map {
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

  def saveArticle() = Action.async(parse.json) {
    implicit request =>
      var json = request.body.as[JsObject]
      if (json("id").as[Int] == 0) {
        json ++= Json.obj("id" -> articleService.ARTICLE_AUTO_ID.incrementAndGet())
        json ++= Json.obj("order" -> articleService.getOrder(json("column").as[Int]))
      }
      val article = json.as[Article]
      articleService.saveArticle(article).map { _ =>
        Ok(Json.obj("info" -> "add article success!"))
      }
  }

  //草稿箱 暂时不做
}
