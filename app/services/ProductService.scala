package services

import java.util.concurrent.atomic.AtomicInteger

import models.{Category, Comment, Department, Product}
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait ProductService {
  val CATEGORY_AUTO_ID = new AtomicInteger()
  val PRODUCT_AUTO_ID = new AtomicInteger()
  val DEPARTMENT_AUTO_ID = new AtomicInteger()

  def initProduct(data: JsArray)
  def initCategory(data: JsArray)
  def initDepartment(data: JsArray)

  def getLastDepartmentID(): Future[Option[Int]]
  def getLastCategoryID(): Future[Option[Int]]
  def getLastProductID(): Future[Option[Int]]

  def getProductsByParent(id: Int): Seq[Product]

  def addDepartment(department: Department): Future[Department]
  def addDepartments(data: JsArray): Future[Seq[Department]]
  def addCategory(category: Category): Future[Category]
  def addCategories(data: JsArray): Future[Seq[Category]]
  def addProduct(product: Product): Future[Product]
  def addProducts(products: IndexedSeq[Product]): Future[Seq[Product]]
  def addProducts(data: JsArray): Future[Seq[Product]]

  def findCategory(category: Int): Future[Option[Category]]
  def findCategoriesByDepartment(department: Int): Future[Seq[Category]]
  def findDepartment(department: Int): Future[Option[Department]]
  def findProduct(product: Int): Future[Option[Product]]
  def findProductsByParent(parent: Int): Future[Seq[Product]]
  def findComment(comment: Int): Future[Option[Comment]]
  def findComment(product: Option[Int]): Future[Seq[Comment]]

  def removeDepartment(department: Int): Future[Option[Department]]
  def removeCategory(category: Int): Future[Option[Category]]
  def removeProduct(product: Int): Future[Option[Product]]
  def removeProductWithChildren(product: Int): Future[Boolean]
  def removeCategoryByDepartment(department: Int): Future[Boolean]

  def saveCategory(category: Category): Future[Option[Category]]
  def updateCategory(selector: BSONDocument, category: Category): Future[Option[Category]]
  def updateProduct(selector: BSONDocument, product: Product): Future[Option[Product]]
  def updateDepartment(selector: BSONDocument, department: Department): Future[Option[Department]]
  def saveDepartment(department: Department): Future[Option[Department]]

  def updateProductCategory(category: Int, id: Int): Future[Seq[Option[Product]]]
  def updateCategoryBanner(cate: Int, filename: String)

  def updateOrSaveProduct(product: Product): Future[Option[Product]]

  def queryDepartments(): Future[Seq[Department]]
  def queryCategories(num: Option[Int] = None): Future[Seq[Category]]
  def queryTopCategories(): Future[Seq[Category]]
  def queryProducts(num: Option[Int] = None): Future[Seq[Product]]
  def queryComments(): Future[Seq[Comment]]
  def queryTopProducts(): Seq[Product]

  def getDepartmentOrder(): Int
}
