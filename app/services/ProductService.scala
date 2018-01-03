package services

import java.util.concurrent.atomic.AtomicInteger

import models.{Category, Comment, Product}
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait ProductService {
  val CATEGORY_AUTO_ID = new AtomicInteger()
  val PRODUCT_AUTO_ID = new AtomicInteger()

  def initProduct(data: JsArray)
  def initCategory(data: JsArray)

  def getLastCategoryID(): Future[Option[Int]]
  def getLastProductID(): Future[Option[Int]]

  def addCategory(category: Category): Future[Category]
  def addCategories(data: JsArray): Future[Seq[Category]]
  def addProduct(product: Product): Future[Product]
  def addProducts(products: IndexedSeq[Product]): Future[Seq[Product]]
  def addProducts(data: JsArray): Future[Seq[Product]]

  def findCategory(category: Int): Future[Option[Category]]
  def findProduct(product: Int): Future[Option[Product]]
  def findProductsByParent(parent: Int): Future[Seq[Product]]
  def findComment(comment: Int): Future[Option[Comment]]
  def findComment(product: Option[Int]): Future[Seq[Comment]]

  def removeCategory(category: Int): Future[Option[Category]]
  def removeProduct(product: Int): Future[Option[Product]]

  def updateCategory(selector: BSONDocument, category: Category): Future[Option[Category]]
  def updateProduct(selector: BSONDocument, product: Product): Future[Option[Product]]

  def updateProductCategory(category: Int, id: Int): Future[Seq[Option[Product]]]
  def updateCategoryBanner(cate: Int, filename: String)

  def queryCategories(): Future[Seq[Category]]
  def queryTopCategories(): Future[Seq[Category]]
  def queryProducts(): Future[Seq[Product]]
  def queryComments(): Future[Seq[Comment]]
}
