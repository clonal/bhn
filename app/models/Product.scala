package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Product(id: Int, name: String, desc: String,
                   category: Array[Int], sku: Array[Int], amazonLink: String)

object Product {
  implicit object ProductReader extends BSONDocumentReader[Product] {
    def read(bson: BSONDocument): Product = {
      val opt: Option[Product] = for {
        id <- bson.getAs[Int]("id")
        name <- bson.getAs[String]("name")
        desc <- bson.getAs[String]("desc")
        category <- bson.getAs[Array[Int]]("category")
        sku <- bson.getAs[Array[Int]]("sku")
      } yield {
        new Product(id, name, desc, category, sku, bson.getAs[String]("amazonLink").getOrElse(""))
      }
      opt.get
    }
  }

  implicit object ProductWriter extends BSONDocumentWriter[Product] {
    def write(product: Product): BSONDocument =
      BSONDocument("id" -> product.id,
        "name" -> product.name,
        "desc" -> product.desc,
        "category" -> product.category,
        "sku" -> product.sku,
        "amazonLink" -> product.amazonLink)
  }

  implicit val productFormat: OFormat[Product] = Json.format[Product]
}
