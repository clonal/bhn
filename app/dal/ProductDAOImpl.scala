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


  override def findProductsByParent(parent: Int) = {
    collection.flatMap(_.find(BSONDocument("parent" -> parent)).cursor[Product].collect[List]())
  }

  override def addProducts(data: JsArray) = {
    val f = for(product <- data.value) yield {
      save(product.as[Product])
//      addProduct(product.as[JsObject])
    }
    Future.sequence(f)
  }

  /*override def addProduct(product: JsObject) = {
    collection.flatMap(_.insert(product)).map{ _ =>
      new Sku(
        (product \ "id").as[Int],
        (product \ "item").as[Int],
        (product \ "name").as[String],
        (product \ "product").as[String],
        (product \ "attributes").as[Array[Map[String, String]]],
        (product \ "content").as[String],
        (product \ "price").as[Double],
        (product \ "sellPrice").as[Double],
        (product \ "asin").as[String],
        (product \ "stock").as[Int],
        (product \ "show").as[Int],
        (product \ "images").as[Map[String, String]]
      )}
  }*/
}
