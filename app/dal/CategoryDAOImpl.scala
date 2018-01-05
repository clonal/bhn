package dal

import javax.inject.Inject

import models.Category
import play.api.libs.json.{JsArray, JsObject}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class CategoryDAOImpl @Inject()(implicit ec: ExecutionContext,
                                reactiveMongoApi: ReactiveMongoApi) extends CategoryDAO{
  override def collection = reactiveMongoApi.database.map(_.collection("category"))

  override def addCategories(data: JsArray) = {
    val f = for(category <- data.value) yield {
      save(category.as[Category])
    }
    Future.sequence(f)
  }
}
