package dal

import models.Column
import play.api.libs.json.JsArray

import scala.concurrent.Future

trait ColumnDAO extends BaseDAO {
  def getChildrenColumn(id: Int):Future[Seq[Column]]

  def getColumn(name: String): Future[Option[Column]]

  def addColumns(data: JsArray): Future[Seq[Column]]

}
