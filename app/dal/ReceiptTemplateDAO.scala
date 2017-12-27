package dal

import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.Future

trait ReceiptTemplateDAO extends BaseDAO{
  def addTemplate(data: JsArray):Future[IndexedSeq[(Int,String)]]
  def addTemplate(data: JsObject):Future[(Int,String)]
}
