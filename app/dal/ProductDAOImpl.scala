package dal

import javax.inject.Inject

import models.Product
import play.api.libs.json.{JsArray, JsObject, Json}
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
      val js = product.as[JsObject] ++ Json.obj("children" -> Seq.empty[Product])
      save(js.as[Product])
    }
    Future.sequence(f)
  }
}
