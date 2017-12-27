package dal

import javax.inject.Inject

import play.api.libs.json.{JsArray, JsObject}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class ReceiptTemplateDAOImpl @Inject()(implicit ec: ExecutionContext,
                                       reactiveMongoApi: ReactiveMongoApi) extends ReceiptTemplateDAO {
  override def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("receipt_template"))

  override def addTemplate(data: JsArray) = {
    val f = for(product <- data.value) yield {
      addTemplate(product.as[JsObject])
    }
    Future.sequence(f)
  }

  override def addTemplate(template: JsObject): Future[(Int, String)] = {
    collection.flatMap(_.insert(template)).map{ _ =>
      (
        (template \ "id").as[Int],
        (template \ "name").as[String]
      )}
  }
}
