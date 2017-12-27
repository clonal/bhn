package services
import javax.inject.{Inject, Singleton}

import dal.ArticleDAO
import models.Article
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext

@Singleton
class ArticleServiceImpl @Inject()(dao: ArticleDAO)
                                  (implicit ex: ExecutionContext) extends ArticleService{

  override def init(data: JsArray): Unit = {
    dao.isEmpty.flatMap{
      case true => addArticles(data)
    } andThen { case _ =>
      getLastID().foreach {
        case Some(i) => ARTICLE_AUTO_ID.set(i)
      }
    }
  }

  override def addArticles(data: JsArray) = {
    dao.addArticles(data)
  }

  override def getLastID() = {
    dao.getLastID
  }

  override def queryAllArticles() = {
    dao.queryArticles()
  }

  override def queryArticles(menu: Int, asc: Int) = {
    dao.queryArticles(menu, asc)
  }

  override def findArticle(article: Int) = {
    dao.find[Article](article)
  }

  override def findArticleByOrder(menu: Int, order: Int) = {
    dao.findByOrder(menu, order)
  }

  override def updateArticle(article: Article) = {
    dao.update(article).map(_ => article)
  }

  override def updateArticle(selector: BSONDocument, modifier: BSONDocument) = {
    dao.update(selector, BSONDocument("$set" -> modifier))
  }

  override def removeArticle(article: Int) = {
    dao.remove(article)
  }

  override def addArticle(data: JsObject) = {
    dao.addArticle(data)
  }
}
