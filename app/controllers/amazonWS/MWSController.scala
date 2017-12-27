package controllers.amazonWS

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import jobs.FindNext
import models.Receipt
import org.joda.time.{DateTimeZone, LocalDateTime}
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesProvider}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.libs.ws.{EmptyBody, WSClient}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.MWSService
import utils.SignatureUtil

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


class MWSController  @Inject() (components: ControllerComponents,
                                config: Configuration,
                                mwsService: MWSService,
                                system: ActorSystem,
                                mailerClient: MailerClient,
                                @Named("send-email-with-next") sendNextEmailActor: ActorRef,
                                ws: WSClient)(implicit ec: ExecutionContext) extends AbstractController(components) with I18nSupport{


    def sendMails(from: Option[String], to: Option[String], template: Option[Int]) = Action.async{
      implicit request =>
        val now = LocalDateTime.now(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val map = mwsService.queryMap(config.get[String]("mws.action.listOrders"), now,
          config.get[String]("mws.marketplaceId.US"), from, to, "")
        val url = SignatureUtil.genURL(config.get[String]("mws.serviceUrl"), config.get[String]("mws.secretKey"),
          config.get[String]("mws.algorithm"), map)

        val tmp = template.getOrElse(1)
        ws.url(url).post(EmptyBody).map { response =>
          val node = response.xml \ "ListOrdersResult" \ "Orders" \ "Order"
          val receipts = node.map { n =>
            Receipt(mwsService.RECEIPT_ID.incrementAndGet(),
              (n \ "SellerOrderId").text,
              (n \ "AmazonOrderId").text,
              (n \ "BuyerName").text,
              (n \ "BuyerEmail").text,
              tmp,
              now
            )
          }
          //处理receipts,过滤条件，以前是否发过, 是否存过
//          mwsService.addReceipts(receipts)
          receipts.foreach { r =>
            mwsService.findReceipt(r).foreach{
              case Some(receipt) => mwsService.addReceipt(receipt)
            }
          }
          val receivers = receipts map (_.buyerEmail)
          sendMail(receivers)


          println(s"=============${receipts.size}")
          receipts foreach { r =>
            println(s"name: ${r.buyerName}, email: ${r.buyerEmail}")
          }
          println(s"=============")

          val nextToken = response.xml \ "ListOrdersResult" \ "NextToken"
          //定时任务 60秒后第二次
          if(nextToken.nonEmpty) {
            println(s"token not empty")
            system.scheduler.scheduleOnce(60 seconds, sendNextEmailActor, FindNext(tmp, nextToken.text, s => sendMail(s)))
          } else {
            println(s"token is empty")
          }
          Ok("dd")
        }
    }

  def sendMail(to: Seq[String])(implicit provider: MessagesProvider) = {
    mailerClient.send(Email(
      subject = Messages("email.already.signed.up.subject"),
      from = Messages("email.from"),
//      to = to,
      to = Seq("mk26vvpmc5bfv8d@marketplace.amazon.com"),
      bodyText = Some(views.txt.emails.sendReceiptEmail("user").body),
      bodyHtml = Some(views.html.emails.sendReceiptEmail("user").body)
    ))
  }

}
