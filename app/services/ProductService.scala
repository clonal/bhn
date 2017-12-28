package services

import java.util.concurrent.atomic.AtomicInteger

import models.{Category, Comment, Item, Product}
import play.api.libs.json.JsArray
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait ProductService {
  val CATEGORY_AUTO_ID = new AtomicInteger()
  val ITEM_AUTO_ID = new AtomicInteger()
  val PRODUCT_AUTO_ID = new AtomicInteger()

  def getItem(item: Int): Option[Item]

  def initProduct(data: JsArray)
  def initCategory(data: JsArray)
  def initItem(data: JsArray)

  def getLastCategoryID(): Future[Option[Int]]
  def getLastItemID(): Future[Option[Int]]
  def getLastProductID(): Future[Option[Int]]

  def addCategory(category: Category): Future[Category]
  def addCategories(data: JsArray): Future[Seq[Category]]
  def addItem(item: Item): Future[Item]
  def addItems(data: JsArray): Future[Seq[Item]]
  def addProduct(product: Product): Future[Product]
  def addProducts(products: IndexedSeq[Product]): Future[Seq[Product]]
  def addProducts(data: JsArray): Future[Seq[Product]]

  def findItem(item: Int): Future[Option[Item]]
  def findCategory(category: Int): Future[Option[Category]]
  def findProduct(product: Int): Future[Option[Product]]
  def findProductsByItem(item: Int): Future[Seq[Product]]
  def findComment(comment: Int): Future[Option[Comment]]
  def findComment(item: Option[Int], product: Option[Int]): Future[Seq[Comment]]

  def removeCategory(category: Int): Future[Option[Category]]
  def removeItem(item: Int): Future[Option[Item]]
  def removeProduct(product: Int): Future[Option[Product]]

  def updateCategory(selector: BSONDocument, category: Category): Future[Option[Category]]
  def updateItem(selector: BSONDocument, item: Item): Future[Option[Item]]
  def updateProduct(selector: BSONDocument, product: Product): Future[Option[Product]]

  def updateItemCategory(category: Int, id: Int): Future[Seq[Option[Item]]]
  def updateItemProduct(product: Int, id: Int): Future[Seq[Option[Item]]]
  def updateCategoryBanner(cate: Int, filename: String)

  def queryCategories(): Future[Seq[Category]]
  def queryTopCategories(): Future[Seq[Category]]
  def queryItems(): Future[Seq[Item]]
  def queryProducts(): Future[Seq[Product]]
  def queryComments(): Future[Seq[Comment]]
}
