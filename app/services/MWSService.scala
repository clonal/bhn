package services

import java.util.concurrent.atomic.AtomicInteger

import models.Receipt
import play.api.libs.json.JsArray

import scala.concurrent.Future

trait MWSService {
  val RECEIPT_ID = new AtomicInteger()
  val RECEIPT_TEMPLATE_ID = new AtomicInteger()

  def queryMap(action: String, time: String, market: String,
               from: Option[String], to: Option[String], nextToken: String): Map[String, String]

  def findReceipt(receipt: Receipt): Future[Option[Receipt]]
  def initReceiptTemplate(data: JsArray)

  def addReceipt(receipt: Receipt): Future[Receipt]
  def addReceipts(receipts: Seq[Receipt]): Future[Seq[Receipt]]
  def addTemplate(data: JsArray): Future[IndexedSeq[(Int, String)]]
  def getLastTemplateID(): Future[Option[Int]]

//  def sendMail(to: Seq[String])
}
