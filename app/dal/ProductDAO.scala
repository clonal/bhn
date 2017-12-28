package dal

import models.Product
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.Future

trait ProductDAO extends BaseDAO{
  def findProductsByItem(item: Int):Future[Seq[Product]]
  def addProducts(data: JsArray): Future[Seq[Product]]
//  def addProduct(data: JsObject): Future[Sku]
}
