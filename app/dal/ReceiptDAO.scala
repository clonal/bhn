package dal

import models.Receipt

import scala.concurrent.Future

trait ReceiptDAO extends BaseDAO{
  def findReceipt(receipt: Receipt): Future[Option[Receipt]]
}
