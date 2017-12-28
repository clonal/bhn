package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Item(id: Int, name: String, desc: String,
                category: Array[Int], product: Array[Int], amazonLink: String)

object Item {
  implicit object ItemReader extends BSONDocumentReader[Item] {
    def read(bson: BSONDocument): Item = {
      val opt: Option[Item] = for {
        id <- bson.getAs[Int]("id")
        name <- bson.getAs[String]("name")
        desc <- bson.getAs[String]("desc")
        category <- bson.getAs[Array[Int]]("category")
        product <- bson.getAs[Array[Int]]("product")
      } yield {
        new Item(id, name, desc, category, product, bson.getAs[String]("amazonLink").getOrElse(""))
      }
      opt.get
    }
  }

  implicit object ItemWriter extends BSONDocumentWriter[Item] {
    def write(item: Item): BSONDocument =
      BSONDocument("id" -> item.id,
        "name" -> item.name,
        "desc" -> item.desc,
        "category" -> item.category,
        "product" -> item.product,
        "amazonLink" -> item.amazonLink)
  }

  implicit val itemFormat: OFormat[Item] = Json.format[Item]
}
