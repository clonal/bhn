package dal

import models.Department
import play.api.libs.json.JsArray

import scala.concurrent.Future

trait DepartmentDAO extends BaseDAO{
  def addDepartments(data: JsArray): Future[scala.Seq[Department]]

}
