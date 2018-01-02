package services

import javax.inject.{Inject, Singleton}

import dal.{CategoryDAO, CommentDAO, ItemDAO, ProductDAO}
import models.{Category, Comment, Item, Product}
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class ProductServiceImpl  @Inject()(itemDAO: ItemDAO,
                                    commentDAO: CommentDAO,
                                    categoryDAO: CategoryDAO,
                                    productDAO: ProductDAO )
                                   (implicit ex: ExecutionContext) extends ProductService{

  final var items: Map[Int, Item] = Map.empty
  final var categories: Map[Int, Category] = Map.empty
  final var products: Map[Int, Product] = Map.empty


  override def getItem(item: Int) = {
    items.get(item)
  }

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

  override def initItem(data: JsArray) = {
    itemDAO.isEmpty.flatMap{
      case true => addItems(data)
    } andThen { case _ =>
      getLastItemID().foreach{
        case Some(i) => ITEM_AUTO_ID.set(i)
      }
      itemDAO.findAll[Item].foreach { p =>
        items = p.map(x => x.id -> x).toMap
      }
    }
  }

  override def getLastCategoryID() = {
    categoryDAO.getLastID
  }

  override def getLastItemID() = {
    itemDAO.getLastID
  }

  override def getLastProductID() = {
    productDAO.getLastID
  }

  override def addCategory(category: Category) = {
    categoryDAO.save[Category](category).andThen{
      case Success(c) => categories += c.id -> c
    }
  }

  override def addItem(item: models.Item) = {
    itemDAO.save[Item](item).andThen{
      case Success(p) => items += p.id -> p
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

  override def addItems(data: JsArray) = {
    itemDAO.addItems(data)
  }

  override def addProducts(data: JsArray) = {
    productDAO.addProducts(data)
  }

  override def findItem(item: Int) = {
    if (items.isEmpty) {
      itemDAO.find[Item](item)
    } else {
      Future(items.get(item))
    }
  }

  override def findCategory(category: Int) = {
    categoryDAO.find[Category](category)
  }

  override def findComment(comment: Int) = {
    commentDAO.find[Comment](comment)
  }

  override def findComment(item: Option[Int], product: Option[Int]) = {
    commentDAO.findComment(item, product)
  }

  override def findProduct(product: Int) = {
    productDAO.find[Product](product)
  }

  override def findProductsByItem(item: Int) = {
    productDAO.findProductsByItem(item)
  }

  override def removeCategory(category: Int) = {
    categoryDAO.remove[BSONDocument, Category](BSONDocument("id" -> category)).andThen{
      case Success(x) => x.foreach(c => categories -= c.id)
    }
  }

  override def removeItem(item: Int) = {
    itemDAO.remove[BSONDocument, Item](BSONDocument("id" -> item)).andThen{
      case Success(x) => x.foreach(p => items -= p.id)
    }
  }

  override def removeProduct(product: Int) = {
    productDAO.remove[BSONDocument, Product](BSONDocument("id" -> product)).andThen{
      case Success(x) => x.foreach(s => products -= s.id)
    }
  }


  override def updateItem(selector: BSONDocument, item: Item) = {
    itemDAO.update(selector, item).andThen{
      case Success(p) => items += item.id -> item
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

  override def updateItemCategory(category: Int, id: Int) = {
    Future.sequence(items.values.filter(_.category.contains(category)).toSeq.map { p =>
      updateItem(BSONDocument("id" -> p.id),
        p.copy(category = p.category.updated(p.category.indexOf(category), id)))
    })
  }


  override def updateCategoryBanner(cate: Int, filename: String): Unit = {
    categories.get(cate) foreach { category =>
      updateCategory(BSONDocument("id" -> cate), category.copy(banner = filename))
    }
  }

  override def updateItemProduct(product: Int, id: Int) = {
    Future.sequence(items.values.filter(_.product.contains(product)).toSeq.map { p =>
      updateItem(BSONDocument("id" -> p.id),
        p.copy(product = p.product.updated(p.product.indexOf(product), id)))
    })
  }

  override def queryCategories() = {
//    categoryDAO.findAll[Category]
    Future(categories.values.toSeq)
  }

  override def queryTopCategories() = {
    Future(categories.values.toSeq.filter(_.parent == 0))
  }

  override def queryItems() = {
    Future(items.values.toList)
//    productDAO.findAll[Item]
  }

  override def queryProducts() = {
    productDAO.findAll[Product]
  }

  override def queryComments() = {
    commentDAO.findAll[Comment]
  }
}
