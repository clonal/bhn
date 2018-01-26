package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import reactivemongo.bson._

import scala.collection.mutable.ArrayBuffer

case class Product(id: Int, name: String, sku: String, category: Int, parent: Int,
                   attributes: Array[Map[String, String]], content: String, price: Double, sellPrice: Double,
                   asin: String, stock: Int, show: Boolean, images: Map[String, String], link: String, var children: Seq[Product]) {
  def addChildren(children: Seq[Product]) = {
    this.children = children
    this
  }
}

object Product {
  implicit object ProductReader extends BSONDocumentReader[Product] {
    def read(bson: BSONDocument): Product = {
      val opt: Option[Product] = for {
        id <- bson.getAs[Int]("id")
        name <- bson.getAs[String]("name")
        sku <- bson.getAs[String]("sku")
        category <- bson.getAs[Int]("category")
        parent <- bson.getAs[Int]("parent")
        attributes <- bson.getAs[BSONArray]("attributes")
        content <- bson.getAs[String]("content")
        price <- bson.getAs[Double]("price")
        sellPrice <- bson.getAs[Double]("sellPrice")
        asin <- bson.getAs[String]("asin")
        stock <- bson.getAs[Int]("stock")
        show <- bson.getAs[Boolean]("show")
        images <- bson.getAs[Map[String,String]]("images")
        link <- bson.getAs[String]("link")
      } yield {
        val ab = ArrayBuffer.empty[Map[String, String]]
        attributes.values.foreach{
          case doc: BSONDocument => ab.append(doc.as[Map[String, String]])
        }
        val attr = attributes.values.map {
          case b: BSONDocument => b.as[Map[String, String]]
        }
        new Product(id, name, sku, category, parent, attr.toArray,
          content, price, sellPrice, asin, stock, show, images, link, Seq.empty)
      }
      opt.get
    }
  }

  implicit object ProductWriter extends BSONDocumentWriter[Product] {
    def write(product: Product): BSONDocument =
      BSONDocument("id" -> product.id,
        "name" -> product.name,
        "sku" -> product.sku,
        "category" -> product.category,
        "parent" -> product.parent,
        "attributes" -> product.attributes,
        "content" -> product.content,
        "price" -> product.price,
        "sellPrice" -> product.sellPrice,
        "asin" -> product.asin,
        "stock" -> product.stock,
        "show" -> product.show,
        "images" -> product.images,
        "link" -> product.link)
  }
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}
