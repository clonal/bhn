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
  final var articles: Map[Int, Article] = Map.empty

  override def init(data: JsArray): Unit = {
    dao.isEmpty.flatMap{
      case true => addArticles(data)
    } andThen { case _ =>
      getLastID().foreach {
        case Some(i) => ARTICLE_AUTO_ID.set(i)
      }
      dao.findAll[Article].foreach { s =>
        articles = s.map(x => x.id -> x).toMap
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

  override def queryArticles(column: Int, asc: Int) = {
    dao.queryArticles(column, asc)
  }

  override def findArticle(article: Int) = {
    dao.findOne[Article](article)
  }

  override def findArticleByOrder(column: Int, order: Int) = {
    dao.findByOrder(column, order)
  }

  override def updateArticle(article: Article) = {
    dao.update(article).map(_ => article).andThen{ case _ =>
      articles += article.id -> article
    }
  }

  override def updateArticle(selector: BSONDocument, modifier: BSONDocument) = {
    dao.update(selector, BSONDocument("$set" -> modifier))
  }

  override def removeArticle(article: Int) = {
    dao.removeById(article).andThen{ case _ =>
      articles -= article
    }
  }

  override def addArticle(data: JsObject) = {
    dao.addArticle(data)
  }

  override def getOrder(col: Int) =  {
    val l = articles.values.filter(_.column == col)
    if (l.isEmpty) {
      1
    } else {
      l.maxBy(_.order).order + 1
    }
  }

  override def saveArticle(article: Article) = {
    dao.findAndUpdate[BSONDocument, Article](BSONDocument("id" -> article.id), article).andThen{
      case _ => articles += article.id -> article
    }
  }
}
