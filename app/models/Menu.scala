package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONInteger, BSONString}

case class Menu(id: Int,
                 name: String,
                 parent: Int,
                 banner: Map[String, String],
                 desc: String,
                 content: String,
                 order: Int,
                 required: Boolean)

object Menu {

  implicit object MenuReader extends BSONDocumentReader[Menu] {
    def read(bson: BSONDocument): Menu = {
      val opt: Option[Menu] = for {
        id <- bson.getAs[Int]("id")
        name <- bson.getAs[String]("name")
        parent <- bson.getAs[Int]("parent")
        banner <- bson.getAs[Map[String, String]]("banner")
        desc <- bson.getAs[String]("desc")
        content <- bson.getAs[String]("content")
        order <- bson.getAs[Int]("order")
        required <- bson.getAs[Boolean]("required")
      } yield {
        new Menu(id, name, parent, banner, desc, content, order, required)
      }
      opt.get
    }
  }

  implicit object MenuWriter extends BSONDocumentWriter[Menu] {
    def write(menu: Menu): BSONDocument =
      BSONDocument("id" -> menu.id,
        "name" -> menu.name,
        "parent" -> menu.parent,
        "banner" -> BSONDocument(menu.banner),
        "desc" -> menu.desc,
        "content" -> menu.content,
        "order" -> menu.order,
        "required" -> menu.required)
  }

/*  implicit object MapWriter extends BSONDocumentWriter[Map[String,String]] {
    def write(map: Map[String, String]): BSONDocument =
      BSONDocument(map.map(x => (x._1, BSONString(x._2))))
  }*/

  implicit val menuFormat: OFormat[Menu] = Json.format[Menu]
}
