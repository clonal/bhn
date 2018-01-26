package dal

import javax.inject.Inject

import models.Article
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{JsArray, JsObject}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._


import scala.concurrent.{ExecutionContext, Future}

class ArticleDAOImpl  @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) extends ArticleDAO{

  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("article"))


  override def addArticle(article: JsObject) = {
/*    collection.flatMap(_.insert(article)).map{ _ =>
      new Article(
        (article \ "id").as[Int],
        (article \ "column").as[Int],
        (article \ "title").as[String],
        (article \ "desc").as[String],
        (article \ "content").as[String],
        (article \ "order").as[Int],
        DateTime.parse((article \ "date").as[String], DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
      )}*/
    (article.validate[Article] map { case s => save(s)}).get
  }

  override def addArticles(data: JsArray) = {
    val f = for(article <- data.value) yield {
      addArticle(article.as[JsObject])
    }
    Future.sequence(f)
  }

//  override def getLastID() = {
//    collection.flatMap(_.find(BSONDocument(),
//      BSONDocument("id" -> 1)).sort(BSONDocument("id" -> -1)).one[Int])
//  }

  override def findByOrder(column: Int, order: Int) = {
    collection.flatMap(_.find(BSONDocument("column" -> column, "order" -> order)).one[Article])
  }

  override def queryArticles() = {
    collection.flatMap(_.find(BSONDocument()).cursor[Article].collect[List]())
  }

  override def queryArticles(column: Int, asc: Int) = {
    collection.flatMap(_.find(BSONDocument("column" -> column)).
      sort(BSONDocument("id" -> asc)).cursor[Article].collect[List]())
  }

  override def update(article: Article) = {
    collection.flatMap(_.update(BSONDocument("id" -> article.id), BSONDocument("$set" ->
      BSONDocument("column" -> article.column, "title" -> article.title, "content" -> article.content,
        "desc" -> article.desc, "order" -> article.order)), upsert = false))
  }

/*  override def getLastID() = {
    collection.flatMap(_.find(BSONDocument(),
      BSONDocument("id" -> 1)).sort(BSONDocument("id" -> -1)).one[BSONDocument]).map{
      case Some(o) => o.getAs[Int]("id")
    }
  }*/
}
