package models

import java.util.Date

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Comment(id: Int, item: Int, product: Int, author: Int,
                   title: String, content: String, star: Double, date: DateTime)

object Comment {

  implicit object CommentReader extends BSONDocumentReader[Comment] {
    def read(bson: BSONDocument): Comment = {
      val opt: Option[Comment] = for {
        id <- bson.getAs[Int]("id")
        item <- bson.getAs[Int]("item")
        product <- bson.getAs[Int]("product")
        author <- bson.getAs[Int]("author")
        title <- bson.getAs[String]("title")
        content <- bson.getAs[String]("content")
        star <- bson.getAs[Double]("star")
        date <- bson.getAs[String]("date")
      } yield {

        new Comment(id, item, product, author, title, content, star,
          DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
      }
      opt.get
    }
  }

  implicit object MenuWriter extends BSONDocumentWriter[Comment] {
    def write(comment: Comment): BSONDocument =
      BSONDocument("id" -> comment.id,
        "item" -> comment.item,
        "product" -> comment.product,
        "author" -> comment.author,
        "title" -> comment.title,
        "content" -> comment.content,
        "star" -> comment.star,
        "date" -> comment.date.toDate)
  }

  implicit val dateWrites = new Writes[DateTime] {
    def writes(o: DateTime): JsValue = {
      Json.toJson(o.toString("yyyy-MM-dd HH:mm:ss"))
    }
  }

  implicit val dateReads = new Reads[DateTime] {
    override def reads(json: JsValue) = {
      JsSuccess(DateTime.parse(json.toString(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
    }
  }

  implicit val commentFormat: OFormat[Comment] = Json.format[Comment]
}
