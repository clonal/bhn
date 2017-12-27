package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Receipt(id: Int, sellerOrderId: String, amazonOrderId: String,
                   buyerName: String, buyerEmail: String, template: Int, date: String)

object Receipt {

  implicit object ReceiptReader extends BSONDocumentReader[Receipt] {
    def read(bson: BSONDocument): Receipt = {
      val opt: Option[Receipt] = for {
        id <- bson.getAs[Int]("id")
        sellerOrderId <- bson.getAs[String]("sellerOrderId")
        amazonOrderId <- bson.getAs[String]("amazonOrderId")
        buyerName <- bson.getAs[String]("buyerName")
        buyerEmail <- bson.getAs[String]("buyerEmail")
        template <- bson.getAs[Int]("template")
        date <- bson.getAs[String]("date")
      } yield {
        new Receipt(id, sellerOrderId, amazonOrderId, buyerName, buyerEmail, template, date)
      }
      opt.get
    }
  }

  implicit object ReceiptWriter extends BSONDocumentWriter[Receipt] {
    def write(receipt: Receipt): BSONDocument =
      BSONDocument("id" -> receipt.id,
        "sellerOrderId" -> receipt.sellerOrderId,
        "amazonOrderId" -> receipt.amazonOrderId,
        "buyerName" -> receipt.buyerName,
        "buyerEmail" -> receipt.buyerEmail,
        "template" -> receipt.template,
        "date" -> receipt.date)
  }

  implicit val receiptFormat: OFormat[Receipt] = Json.format[Receipt]
}
