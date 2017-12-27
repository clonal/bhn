package services

import java.util.concurrent.atomic.AtomicInteger

import models.{Category, Comment, Product, Sku}
import play.api.libs.json.JsArray
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait ProductService {
  def updateCategoryBanner(cate: Int, filename: String)

  val CATEGORY_AUTO_ID = new AtomicInteger()
  val PRODUCT_AUTO_ID = new AtomicInteger()
  val SKU_AUTO_ID = new AtomicInteger()

  def getProduct(product: Int): Option[Product]

  def initSku(data: JsArray)
  def initCategory(data: JsArray)
  def initProduct(data: JsArray)

  def getLastCategoryID(): Future[Option[Int]]
  def getLastProductID(): Future[Option[Int]]
  def getLastSkuID(): Future[Option[Int]]

  def addCategory(category: Category): Future[Category]
  def addCategories(data: JsArray): Future[Seq[Category]]
  def addProduct(product: Product): Future[Product]
  def addProducts(data: JsArray): Future[Seq[Product]]
  def addSku(sku: Sku): Future[Sku]
  def addSkus(sku: IndexedSeq[Sku]): Future[Seq[Sku]]
  def addSkus(data: JsArray): Future[Seq[Sku]]

  def findProduct(product: Int): Future[Option[Product]]
  def findCategory(category: Int): Future[Option[Category]]
  def findSku(sku: Int): Future[Option[Sku]]
  def findSkusByProduct(product: Int): Future[Seq[Sku]]
  def findComment(comment: Int): Future[Option[Comment]]
  def findComment(product: Option[Int], sku: Option[Int]): Future[Seq[Comment]]

  def removeCategory(category: Int): Future[Option[Category]]
  def removeProduct(product: Int): Future[Option[Product]]
  def removeSku(sku: Int): Future[Option[Sku]]

  @deprecated
  def updateCategory(selector: BSONDocument, modifier: BSONDocument): Future[UpdateWriteResult]
  @deprecated
  def updateProduct(selector: BSONDocument, modifier: BSONDocument): Future[UpdateWriteResult]
  @deprecated
  def updateSku(selector: BSONDocument, modifier: BSONDocument): Future[UpdateWriteResult]

  def updateCategory(selector: BSONDocument, category: Category): Future[Option[Category]]
  def updateProduct(selector: BSONDocument, product: Product): Future[Option[Product]]
  def updateSku(selector: BSONDocument, sku: Sku): Future[Option[Sku]]

  def updateProductCategory(category: Int, id: Int): Future[Seq[Option[Product]]]
  def updateProductSku(sku: Int, id: Int): Future[Seq[Option[Product]]]

  def queryCategories(): Future[Seq[Category]]
  def queryTopCategories(): Future[Seq[Category]]
  def queryProducts(): Future[Seq[Product]]
  def querySkus(): Future[Seq[Sku]]
  def queryComments(): Future[Seq[Comment]]
}
