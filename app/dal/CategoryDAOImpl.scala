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
      addCategory(category.as[JsObject])
    }
    Future.sequence(f)
  }

  override def addCategory(category: JsObject) = {
    collection.flatMap(_.insert(category)).map{ _ =>
      new Category(
        (category \ "id").as[Int],
        (category \ "name").as[String],
        (category \ "desc").as[String],
        (category \ "parent").as[Int],
        (category \ "banner").as[String]
      )}
  }
}
