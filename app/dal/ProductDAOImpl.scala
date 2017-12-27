package dal

import javax.inject.Inject

import models.Product
import play.api.libs.json.{JsArray, JsObject}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class  ProductDAOImpl @Inject()(implicit ec: ExecutionContext,
                      reactiveMongoApi: ReactiveMongoApi)extends ProductDAO{
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("product"))

  override def addProducts(data: JsArray) = {
    val f = for(product <- data.value) yield {
      addProduct(product.as[JsObject])
    }
    Future.sequence(f)
  }

  override def addProduct(product: JsObject) = {
    collection.flatMap(_.insert(product)).map{ _ =>
      new Product(
        (product \ "id").as[Int],
        (product \ "name").as[String],
        (product \ "desc").as[String],
        (product \ "category").as[Array[Int]],
        (product \ "sku").as[Array[Int]],
        (product \ "amazonLink").as[String]
      )}
  }
}
