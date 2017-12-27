package dal

import javax.inject.Inject

import models.Sku
import play.api.libs.json.{JsArray, JsObject}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class SkuDAOImpl  @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) extends SkuDAO{
  override def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("sku"))


  override def findSkusByProduct(product: Int) = {
    collection.flatMap(_.find(BSONDocument("product" -> product)).cursor[Sku].collect[List]())
  }

  override def addSkus(data: JsArray) = {
    val f = for(sku <- data.value) yield {
      addSku(sku.as[JsObject])
    }
    Future.sequence(f)
  }

  override def addSku(sku: JsObject) = {
    collection.flatMap(_.insert(sku)).map{ _ =>
      new Sku(
        (sku \ "id").as[Int],
        (sku \ "product").as[Int],
        (sku \ "name").as[String],
        (sku \ "attributes").as[Array[Map[String, String]]],
        (sku \ "content").as[String],
        (sku \ "price").as[Double],
        (sku \ "sellPrice").as[Double],
        (sku \ "asin").as[String],
        (sku \ "stock").as[Int],
        (sku \ "show").as[Int],
        (sku \ "images").as[Map[String, String]]
      )}
  }
}
