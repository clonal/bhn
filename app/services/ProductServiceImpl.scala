package services

import javax.inject.{Inject, Singleton}

import dal.{CategoryDAO, CommentDAO, DepartmentDAO, ProductDAO}
import models.{Category, Comment, Department, Product}
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class ProductServiceImpl  @Inject()(commentDAO: CommentDAO,
                                    departmentDAO: DepartmentDAO,
                                    categoryDAO: CategoryDAO,
                                    productDAO: ProductDAO )
                                   (implicit ex: ExecutionContext) extends ProductService{

  final var departments: Map[Int, Department] = Map.empty
  final var categories: Map[Int, Category] = Map.empty
  final var products: Map[Int, Product] = Map.empty


  override def initProduct(data: JsArray) = {
    productDAO.isEmpty.flatMap{
      case true => addProducts(data)
    } andThen { case _ =>
      getLastProductID().foreach{
        case Some(i) => PRODUCT_AUTO_ID.set(i)
      }
      productDAO.findAll[Product].foreach { s =>
        products = s.map(x => x.id -> x).toMap
      }
    }
  }

  override def initCategory(data: JsArray) = {
    categoryDAO.isEmpty.flatMap{
      case true => addCategories(data)
    } andThen { case _ =>
      getLastCategoryID().foreach{
        case Some(i) =>
          CATEGORY_AUTO_ID.set(i)
      }
      categoryDAO.findAll[Category].foreach { c =>
        categories = c.map(x => x.id -> x).toMap
      }
    }
  }

  override def initDepartment(data: JsArray): Unit = {
    departmentDAO.isEmpty.flatMap{
      case true => addDepartments(data)
    } andThen { case _ =>
      getLastDepartmentID().foreach{
        case Some(i) =>
          DEPARTMENT_AUTO_ID.set(i)
      }
      departmentDAO.findAll[Department].foreach { d =>
        departments = d.map(x => x.id -> x).toMap
      }
    }
  }

  override def getLastDepartmentID() = {
    departmentDAO.getLastID
  }

  override def getLastCategoryID() = {
    categoryDAO.getLastID
  }

  override def getLastProductID() = {
    productDAO.getLastID
  }

  override def addCategory(category: Category) = {
    categoryDAO.save[Category](category).andThen{
      case Success(c) => categories += c.id -> c
    }
  }

  override def addProduct(product: Product) = {
    productDAO.save(product).andThen{
      case Success(s) => products += s.id -> s
    }
  }

  override def addProducts(products: IndexedSeq[Product]) = {
    Future.sequence(products.map{ s =>
      addProduct(s)
    })
  }

  override def addCategories(data: JsArray) = {
    categoryDAO.addCategories(data)
  }

  override def addDepartments(data: JsArray) = {
    departmentDAO.addDepartments(data)
  }

  override def addDepartment(department: Department) = {
    departmentDAO.save(department).andThen{
      case Success(d) => departments += d.id -> d
    }
  }

  override def addProducts(data: JsArray) = {
    productDAO.addProducts(data)
  }

  override def findCategory(category: Int) = {
    categoryDAO.find[Category](category)
  }

  override def findDepartment(department: Int) = {
    departmentDAO.find[Department](department)
  }

  override def findComment(comment: Int) = {
    commentDAO.find[Comment](comment)
  }

  override def findComment(product: Option[Int]) = {
    commentDAO.findComment(product)
  }

  override def findProduct(product: Int) = {
    productDAO.find[Product](product)
  }

  override def findProductsByParent(parent: Int) = {
    productDAO.findProductsByParent(parent)
  }

  override def removeCategory(category: Int) = {
    categoryDAO.remove[BSONDocument, Category](BSONDocument("id" -> category)).andThen{
      case Success(x) => x.foreach(c => categories -= c.id)
    }
  }

  override def removeDepartment(department: Int) = {
    departmentDAO.remove[BSONDocument, Department](BSONDocument("id" -> department)).andThen{
      case Success(x) => x.foreach(d => departments -= d.id)
    }
  }

  override def removeProduct(product: Int) = {
    productDAO.remove[BSONDocument, Product](BSONDocument("id" -> product)).andThen{
      case Success(x) => x.foreach(s => products -= s.id)
    }
  }

  override def updateCategory(selector: BSONDocument, category: Category) = {
    categoryDAO.update(selector, category).andThen{
      case Success(_) => categories += category.id -> category
    }
  }

  override def updateProduct(selector: BSONDocument, product: Product) = {
    productDAO.update(selector, product).andThen{
      case Success(_) => products += product.id -> product
    }
  }

  override def updateDepartment(selector: BSONDocument, department: Department) = {
    departmentDAO.update(selector, department).andThen{
      case Success(_) => departments += department.id -> department
    }
  }

  override def updateProductCategory(category: Int, id: Int) = {
    Future.sequence(products.values.filter(_.category == category).toSeq.map { p =>
      updateProduct(BSONDocument("id" -> p.id),
        p.copy(category = id))
    })
  }


  override def updateCategoryBanner(cate: Int, filename: String): Unit = {
    categories.get(cate) foreach { category =>
      updateCategory(BSONDocument("id" -> cate), category.copy(banner = filename))
    }
  }

  override def queryCategories() = {
//    categoryDAO.findAll[Category]
    Future(categories.values.toSeq)
  }

  override def queryDepartments() = {
    Future(departments.values.toSeq)
  }

  override def queryTopCategories() = {
    Future(categories.values.toSeq.filter(_.department == 0))
  }

  override def queryProducts() = {
    productDAO.findAll[Product]
  }

  override def queryComments() = {
    commentDAO.findAll[Comment]
  }

  override def updateOrSaveProduct(product: Product) = {
    productDAO.update(BSONDocument("id" -> product.id), product)
  }
}
