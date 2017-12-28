package dal

import javax.inject.Inject

import models.Item
import play.api.libs.json.{JsArray, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class  ItemDAOImpl @Inject()(implicit ec: ExecutionContext,
                             reactiveMongoApi: ReactiveMongoApi)extends ItemDAO{
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("item"))

  override def addItems(data: JsArray) = {
    val f = for(item <- data.value) yield {
      save(item.as[Item])
    }
    Future.sequence(f)
  }

}
