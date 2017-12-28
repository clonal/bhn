package services

import java.util.concurrent.atomic.AtomicInteger

import models.{Category, Comment, Item, Sku}
import play.api.libs.json.JsArray
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait ProductService {
  val CATEGORY_AUTO_ID = new AtomicInteger()
  val ITEM_AUTO_ID = new AtomicInteger()
  val SKU_AUTO_ID = new AtomicInteger()

  def getItem(item: Int): Option[Item]

  def initSku(data: JsArray)
  def initCategory(data: JsArray)
  def initItem(data: JsArray)

  def getLastCategoryID(): Future[Option[Int]]
  def getLastItemID(): Future[Option[Int]]
  def getLastSkuID(): Future[Option[Int]]

  def addCategory(category: Category): Future[Category]
  def addCategories(data: JsArray): Future[Seq[Category]]
  def addItem(item: Item): Future[Item]
  def addItems(data: JsArray): Future[Seq[Item]]
  def addSku(sku: Sku): Future[Sku]
  def addSkus(sku: IndexedSeq[Sku]): Future[Seq[Sku]]
  def addSkus(data: JsArray): Future[Seq[Sku]]

  def findItem(item: Int): Future[Option[Item]]
  def findCategory(category: Int): Future[Option[Category]]
  def findSku(sku: Int): Future[Option[Sku]]
  def findSkusByItem(item: Int): Future[Seq[Sku]]
  def findComment(comment: Int): Future[Option[Comment]]
  def findComment(item: Option[Int], sku: Option[Int]): Future[Seq[Comment]]

  def removeCategory(category: Int): Future[Option[Category]]
  def removeItem(item: Int): Future[Option[Item]]
  def removeSku(sku: Int): Future[Option[Sku]]

  def updateCategory(selector: BSONDocument, category: Category): Future[Option[Category]]
  def updateItem(selector: BSONDocument, item: Item): Future[Option[Item]]
  def updateSku(selector: BSONDocument, sku: Sku): Future[Option[Sku]]

  def updateItemCategory(category: Int, id: Int): Future[Seq[Option[Item]]]
  def updateItemSku(sku: Int, id: Int): Future[Seq[Option[Item]]]
  def updateCategoryBanner(cate: Int, filename: String)

  def queryCategories(): Future[Seq[Category]]
  def queryTopCategories(): Future[Seq[Category]]
  def queryItems(): Future[Seq[Item]]
  def querySkus(): Future[Seq[Sku]]
  def queryComments(): Future[Seq[Comment]]
}
