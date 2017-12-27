package services

import javax.inject.{Inject, Singleton}

import dal.{CategoryDAO, CommentDAO, ProductDAO, SkuDAO}
import models.{Category, Comment, Product, Sku}
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class ProductServiceImpl  @Inject()(productDAO: ProductDAO,
                                     commentDAO: CommentDAO,
                                     categoryDAO: CategoryDAO,
                                     skuDAO: SkuDAO )
                                   (implicit ex: ExecutionContext) extends ProductService{

  final var products: Map[Int, Product] = Map.empty
  final var categories: Map[Int, Category] = Map.empty
  final var skus: Map[Int, Sku] = Map.empty


  override def getProduct(product: Int) = {
    products.get(product)
  }

  override def initSku(data: JsArray): Unit = {
    skuDAO.isEmpty.flatMap{
      case true => addSkus(data)
    } andThen { case _ =>
      getLastSkuID().foreach{
        case Some(i) => SKU_AUTO_ID.set(i)
      }
      skuDAO.findAll[Sku].foreach { s =>
        skus = s.map(x => x.id -> x).toMap
      }
    }
  }

  override def initCategory(data: JsArray): Unit = {
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

  override def initProduct(data: JsArray): Unit = {
    productDAO.isEmpty.flatMap{
      case true => addProducts(data)
    } andThen { case _ =>
      getLastProductID().foreach{
        case Some(i) => PRODUCT_AUTO_ID.set(i)
      }
      productDAO.findAll[Product].foreach { p =>
        products = p.map(x => x.id -> x).toMap
      }
    }
  }

  override def getLastCategoryID() = {
    categoryDAO.getLastID
  }

  override def getLastProductID() = {
    productDAO.getLastID
  }

  override def getLastSkuID() = {
    skuDAO.getLastID
  }

  override def addCategory(category: Category) = {
    categoryDAO.save[Category](category).andThen{
      case Success(c) => categories += c.id -> c
    }
  }

  override def addProduct(product: models.Product) = {
    productDAO.save[Product](product).andThen{
      case Success(p) => products += p.id -> p
    }
  }

  override def addSku(sku: Sku) = {
    skuDAO.save[Sku](sku).andThen{
      case Success(s) => skus += s.id -> s
    }
  }

  override def addSkus(sku: IndexedSeq[Sku]) = {
    Future.sequence(sku.map{ s =>
      addSku(s)
    })
  }

  override def addCategories(data: JsArray) = {
    categoryDAO.addCategories(data)
  }

  override def addProducts(data: JsArray) = {
    productDAO.addProducts(data)
  }

  override def addSkus(data: JsArray) = {
    skuDAO.addSkus(data)
  }

  override def findProduct(product: Int) = {
    productDAO.find[Product](product)
  }

  override def findCategory(category: Int) = {
    categoryDAO.find[Category](category)
  }

  override def findComment(comment: Int) = {
    commentDAO.find[Comment](comment)
  }

  override def findComment(product: Option[Int], sku: Option[Int]) = {
    commentDAO.findComment(product, sku)
  }

  override def findSku(sku: Int) = {
    skuDAO.find[Sku](sku)
  }

  override def findSkusByProduct(product: Int) = {
    skuDAO.findSkusByProduct(product)
  }

  override def removeCategory(category: Int) = {
    categoryDAO.remove[BSONDocument, Category](BSONDocument("id" -> category)).andThen{
      case Success(x) => x.foreach(c => categories -= c.id)
    }
  }

  override def removeProduct(product: Int) = {
    productDAO.remove[BSONDocument, Product](BSONDocument("id" -> product)).andThen{
      case Success(x) => x.foreach(p => products -= p.id)
    }
  }

  override def removeSku(sku: Int) = {
    skuDAO.remove[BSONDocument, Sku](BSONDocument("id" -> sku)).andThen{
      case Success(x) => x.foreach(s => skus -= s.id)
    }
  }

  override def updateCategory(selector: BSONDocument, modifier: BSONDocument) = {
    categoryDAO.update(selector, BSONDocument("$set" -> modifier))
  }

  override def updateProduct(selector: BSONDocument, modifier: BSONDocument) = {
    productDAO.update(selector, BSONDocument("$set" -> modifier))
  }

  override def updateProduct(selector: BSONDocument, product: Product) = {
    productDAO.update(selector, product).andThen{
      case Success(p) => products += product.id -> product
    }
  }

  override def updateCategory(selector: BSONDocument, category: Category) = {
    categoryDAO.update(selector, category).andThen{
      case Success(_) => categories += category.id -> category
    }
  }

  override def updateSku(selector: BSONDocument, sku: Sku) = {
    skuDAO.update(selector, sku).andThen{
      case Success(_) => skus += sku.id -> sku
    }
  }

  override def updateProductCategory(category: Int, id: Int) = {
    Future.sequence(products.values.filter(_.category.contains(category)).toSeq.map { p =>
      updateProduct(BSONDocument("id" -> p.id),
        p.copy(category = p.category.updated(p.category.indexOf(category), id)))
    })
  }


  override def updateCategoryBanner(cate: Int, filename: String): Unit = {
    categories.get(cate) foreach { category =>
      updateCategory(BSONDocument("id" -> cate), category.copy(banner = filename))
    }
  }

  override def updateProductSku(sku: Int, id: Int) = {
    Future.sequence(products.values.filter(_.sku.contains(sku)).toSeq.map { p =>
      updateProduct(BSONDocument("id" -> p.id),
        p.copy(sku = p.sku.updated(p.sku.indexOf(sku), id)))
    })
  }

  override def updateSku(selector: BSONDocument, modifier: BSONDocument) = {
    skuDAO.update(selector, BSONDocument("$set" -> modifier))
  }

  override def queryCategories() = {
//    categoryDAO.findAll[Category]
    Future(categories.values.toSeq)
  }

  override def queryTopCategories() = {
    Future(categories.values.toSeq.filter(_.parent == 0))
  }

  override def queryProducts() = {
    Future(products.values.toList)
//    productDAO.findAll[Product]
  }

  override def querySkus() = {
    skuDAO.findAll[Sku]
  }

  override def queryComments() = {
    commentDAO.findAll[Comment]
  }
}
