package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Column(id: Int, name: String, parent: Int, banner: String,
                  desc: String, content: String, order: Int)

object Column {
  implicit object ColumnReader extends BSONDocumentReader[Column] {
    def read(bson: BSONDocument): Column = {
      val opt: Option[Column] = for {
        id <- bson.getAs[Int]("id")
        name <- bson.getAs[String]("name")
        parent <- bson.getAs[Int]("parent")
        banner <- bson.getAs[String]("banner")
        desc <- bson.getAs[String]("desc")
        content <- bson.getAs[String]("content")
        order <- bson.getAs[Int]("order")
      } yield {
        new Column(id, name, parent, banner, desc, content, order)
      }
      opt.get
    }
  }

  implicit object ColumnWriter extends BSONDocumentWriter[Column] {
    def write(column: Column): BSONDocument =
      BSONDocument("id" -> column.id,
        "name" -> column.name,
        "parent" -> column.parent,
        "banner" -> column.banner,
        "desc" -> column.desc,
        "content" -> column.content,
        "order" -> column.order)
  }

  implicit val columnFormat: OFormat[Column] = Json.format[Column]
}



