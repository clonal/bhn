package services

import javax.inject.{Inject, Singleton}

import dal.{CategoryDAO, CommentDAO, ItemDAO, SkuDAO}
import models.{Category, Comment, Item, Sku}
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class ProductServiceImpl  @Inject()(itemDAO: ItemDAO,
                                    commentDAO: CommentDAO,
                                    categoryDAO: CategoryDAO,
                                    skuDAO: SkuDAO )
                                   (implicit ex: ExecutionContext) extends ProductService{

  final var items: Map[Int, Item] = Map.empty
  final var categories: Map[Int, Category] = Map.empty
  final var skus: Map[Int, Sku] = Map.empty


  override def getItem(item: Int) = {
    items.get(item)
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

  override def initItem(data: JsArray): Unit = {
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

  override def getLastSkuID() = {
    skuDAO.getLastID
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

  override def addItems(data: JsArray) = {
    itemDAO.addItems(data)
  }

  override def addSkus(data: JsArray) = {
    skuDAO.addSkus(data)
  }

  override def findItem(item: Int) = {
    itemDAO.find[Item](item)
  }

  override def findCategory(category: Int) = {
    categoryDAO.find[Category](category)
  }

  override def findComment(comment: Int) = {
    commentDAO.find[Comment](comment)
  }

  override def findComment(item: Option[Int], sku: Option[Int]) = {
    commentDAO.findComment(item, sku)
  }

  override def findSku(sku: Int) = {
    skuDAO.find[Sku](sku)
  }

  override def findSkusByItem(item: Int) = {
    skuDAO.findSkusByItem(item)
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

  override def removeSku(sku: Int) = {
    skuDAO.remove[BSONDocument, Sku](BSONDocument("id" -> sku)).andThen{
      case Success(x) => x.foreach(s => skus -= s.id)
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

  override def updateSku(selector: BSONDocument, sku: Sku) = {
    skuDAO.update(selector, sku).andThen{
      case Success(_) => skus += sku.id -> sku
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

  override def updateItemSku(sku: Int, id: Int) = {
    Future.sequence(items.values.filter(_.sku.contains(sku)).toSeq.map { p =>
      updateItem(BSONDocument("id" -> p.id),
        p.copy(sku = p.sku.updated(p.sku.indexOf(sku), id)))
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
//    productDAO.findAll[Product]
  }

  override def querySkus() = {
    skuDAO.findAll[Sku]
  }

  override def queryComments() = {
    commentDAO.findAll[Comment]
  }
}
