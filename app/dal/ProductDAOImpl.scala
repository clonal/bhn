package dal

import javax.inject.Inject

import models.Product
import play.api.libs.json.{JsArray, JsObject}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class ProductDAOImpl  @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) extends ProductDAO{
  override def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("product"))


  override def findProductsByItem(item: Int) = {
    collection.flatMap(_.find(BSONDocument("item" -> item)).cursor[Product].collect[List]())
  }

  override def addProducts(data: JsArray) = {
    val f = for(product <- data.value) yield {
      save(product.as[Product])
//      addProduct(sku.as[JsObject])
    }
    Future.sequence(f)
  }

  /*override def addProduct(sku: JsObject) = {
    collection.flatMap(_.insert(sku)).map{ _ =>
      new Sku(
        (sku \ "id").as[Int],
        (sku \ "item").as[Int],
        (sku \ "name").as[String],
        (sku \ "sku").as[String],
        (sku \ "attributes").as[Array[Map[String, String]]],
        (sku \ "content").as[String],
        (sku \ "price").as[Double],
        (sku \ "sellPrice").as[Double],
        (sku \ "asin").as[String],
        (sku \ "stock").as[Int],
        (sku \ "show").as[Int],
        (sku \ "images").as[Map[String, String]]
      )}
  }*/
}
