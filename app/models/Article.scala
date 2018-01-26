package models

import java.util.Date

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Article(id: Int,
                   column: Int,
                   title: String,
                   desc: String,
                   content: String,
                   order: Int,
                   date: DateTime)

object Article {
  implicit object ArticleReader extends BSONDocumentReader[Article] {
    def read(bson: BSONDocument): Article = {
      val opt: Option[Article] = for {
        id <- bson.getAs[Int]("id")
        column <- bson.getAs[Int]("column")
        title <- bson.getAs[String]("title")
        desc <- bson.getAs[String]("desc")
        content <- bson.getAs[String]("content")
        order <- bson.getAs[Int]("order")
        date <- bson.getAs[Date]("date")
      } yield {
        new Article(id, column, title, desc, content, order, new DateTime(date))
      }
      opt.get
    }
  }

  implicit object ArticleWriter extends BSONDocumentWriter[Article] {
    def write(article: Article): BSONDocument =
      BSONDocument("id" -> article.id,
        "column" -> article.column,
        "title" -> article.title,
        "desc" -> article.desc,
        "content" -> article.content,
        "order" -> article.order,
        "date" -> article.date.toDate)
  }

/*  implicit val dateWrites = new Writes[DateTime] {
    def writes(o: DateTime): JsValue = {
      Json.toJson(o.toString("yyyy-MM-dd HH:mm:ss"))
    }
  }

  implicit val dateReads = new Reads[DateTime] {
    override def reads(json: JsValue) = {
      JsSuccess(DateTime.parse(json.toString(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
    }
  }*/

  implicit val articleFormat: OFormat[Article] = Json.format[Article]
}
