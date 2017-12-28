package dal

import models.Item
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.Future

trait ItemDAO extends BaseDAO{
  def addItems(data: JsArray): Future[Seq[Item]]
}
