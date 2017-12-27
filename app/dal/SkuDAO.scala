package dal

import models.Sku
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.Future

trait SkuDAO extends BaseDAO{
  def findSkusByProduct(product: Int):Future[Seq[Sku]]
  def addSkus(data: JsArray): Future[Seq[Sku]]
  def addSku(data: JsObject): Future[Sku]
}
