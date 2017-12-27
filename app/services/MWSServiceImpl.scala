package services

import javax.inject.{Inject, Singleton}

import dal.{ReceiptDAO, ReceiptTemplateDAO}
import models.Receipt
import play.api.Configuration
import play.api.libs.json.JsArray
import play.api.libs.mailer.MailerClient
import utils.Common

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MWSServiceImpl @Inject()(config: Configuration, mailerClient: MailerClient, receiptDAO: ReceiptDAO,
                               receiptTemplateDAO: ReceiptTemplateDAO)(implicit ex: ExecutionContext) extends MWSService {


  override def findReceipt(receipt: Receipt): Future[Option[Receipt]] = {
    receiptDAO.findReceipt(receipt)
  }

  def queryMap(action: String, time: String, market: String, from: Option[String], to: Option[String], nextToken: String) = {
    var  parameters = Map.empty[String,String]
    // Add required parameters. Change these as needed.
    parameters += "Action" -> Common.urlEncode(action)
    parameters += "MWSAuthToken" -> Common.urlEncode(config.get[String]("mws.MWSAuthToken"))
    parameters += "SellerId" -> Common.urlEncode(config.get[String]("mws.sellerId"))
    parameters += "AWSAccessKeyId" -> Common.urlEncode(config.get[String]("mws.AWSAccessKeyId"))
    parameters += "SignatureMethod" -> Common.urlEncode(config.get[String]("mws.algorithm"))
    parameters += "SignatureVersion" -> Common.urlEncode("2")
    parameters += "Timestamp" -> Common.urlEncode(time)
    parameters += "Version" -> Common.urlEncode(config.get[String]("mws.version"))
    parameters += "MarketplaceId.Id.1" -> Common.urlEncode(market)


    if (action.equals(config.get[String]("mws.action.listOrders"))) {
      from.foreach { f =>
//        parameters += "CreatedAfter" -> Common.urlEncode(Common.timeFormat(f))
        parameters += "CreatedAfter" -> Common.urlEncode(f)
      }
      to.foreach { t =>
//        parameters += "CreatedBefore" -> Common.urlEncode(Common.timeFormat(t))
        parameters += "CreatedBefore" -> Common.urlEncode(t)
      }
      parameters += "OrderStatus.Status.1" -> Common.urlEncode("Shipped")
      parameters += "OrderStatus.Status.2" -> Common.urlEncode("Unshipped")
      parameters += "OrderStatus.Status.3" -> Common.urlEncode("PartiallyShipped")
    }

    if (action.equals(config.get[String]("mws.action.listOrdersByNext"))) {
      parameters += "NextToken" -> Common.urlEncode("nextToken")
    }
    parameters
  }


  override def addReceipt(receipt: Receipt) = {
    receiptDAO.save[Receipt](receipt)
  }

  override def addReceipts(receipts: Seq[Receipt]) = {
    Future.sequence(receipts.map(x => receiptDAO.save[Receipt](x)))
  }

  override def addTemplate(data: JsArray) = {
    receiptTemplateDAO.addTemplate(data)
  }

  override def getLastTemplateID() = {
    receiptTemplateDAO.getLastID
  }

  def initReceiptTemplate(data: JsArray) {
    receiptTemplateDAO.isEmpty.flatMap{
      case true => addTemplate(data)
    } andThen { case _ =>
      getLastTemplateID().foreach{
        case Some(i) => RECEIPT_TEMPLATE_ID.set(i)
      }
    }
  }

/*  def sendMail(to: Seq[String]) = {
    mailerClient.send(Email(
      subject = Messages("email.already.signed.up.subject"),
      from = Messages("email.from"),
      to = to,
      bodyText = Some(views.txt.sendReceiptEmail("user").body),
      bodyHtml = Some(views.html.sendReceiptEmail("user").body)
    ))
  }*/
}
