package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import reactivemongo.bson._

import scala.collection.mutable.ArrayBuffer

case class Product(id: Int, item: Int, name: String, sku: String,
                   attributes: Array[Map[String, String]], content: String, price: Double,
                   sellPrice: Double, asin: String, stock: Int, show: Int, images: Map[String, String])

object Product {
  implicit object ProductReader extends BSONDocumentReader[Product] {
    def read(bson: BSONDocument): Product = {
      val opt: Option[Product] = for {
        id <- bson.getAs[Int]("id")
        item <- bson.getAs[Int]("item")
        name <- bson.getAs[String]("name")
        sku <- bson.getAs[String]("sku")
        attributes <- bson.getAs[BSONArray]("attributes")
        content <- bson.getAs[String]("content")
        price <- bson.getAs[Double]("price")
        sellPrice <- bson.getAs[Double]("sellPrice")
        asin <- bson.getAs[String]("asin")
        stock <- bson.getAs[Int]("stock")
        show <- bson.getAs[Int]("show")
        images <- bson.getAs[Map[String,String]]("images")
      } yield {
        val ab = ArrayBuffer.empty[Map[String, String]]
        attributes.values.foreach{
          case doc: BSONDocument => ab.append(doc.as[Map[String, String]])
        }
        val attr = attributes.values.map {
          case b: BSONDocument => b.as[Map[String, String]]
        }
        new Product(id, item, name, sku, attr.toArray, content, price, sellPrice, asin, stock, show, images)
      }
      opt.get
    }
  }

  implicit object ProductWriter extends BSONDocumentWriter[Product] {
    def write(product: Product): BSONDocument =
      BSONDocument("id" -> product.id,
        "item" -> product.item,
        "name" -> product.name,
        "sku" -> product.sku,
        "attributes" -> product.attributes,
        "content" -> product.content,
        "price" -> product.price,
        "sellPrice" -> product.sellPrice,
        "asin" -> product.asin,
        "stock" -> product.stock,
        "show" -> product.show,
        "images" -> product.images)
  }
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}
