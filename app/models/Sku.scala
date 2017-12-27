package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import reactivemongo.bson._

import scala.collection.mutable.ArrayBuffer

case class Sku(id: Int, product: Int, name: String,
               attributes: Array[Map[String, String]], content: String, price: Double,
               sellPrice: Double, asin: String, stock: Int, show: Int, images: Map[String, String])

object Sku {
  implicit object SkuReader extends BSONDocumentReader[Sku] {
    def read(bson: BSONDocument): Sku = {
      val opt: Option[Sku] = for {
        id <- bson.getAs[Int]("id")
        product <- bson.getAs[Int]("product")
        name <- bson.getAs[String]("name")
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
        new Sku(id, product, name, attr.toArray, content, price, sellPrice, asin, stock, show, images)
      }
      opt.get
    }
  }

  implicit object SkuWriter extends BSONDocumentWriter[Sku] {
    def write(sku: Sku): BSONDocument =
      BSONDocument("id" -> sku.id,
        "product" -> sku.product,
        "name" -> sku.name,
        "attributes" -> sku.attributes,
        "content" -> sku.content,
        "price" -> sku.price,
        "sellPrice" -> sku.sellPrice,
        "asin" -> sku.asin,
        "stock" -> sku.stock,
        "show" -> sku.show,
        "images" -> sku.images)
  }
  implicit val skuFormat: OFormat[Sku] = Json.format[Sku]
}
