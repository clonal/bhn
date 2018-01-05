package dal

import models.Category
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.Future

trait CategoryDAO extends BaseDAO{
  def addCategories(data: JsArray): Future[Seq[Category]]
}
