package jobs

import javax.inject.Inject

import akka.actor.{Actor, ActorSystem}
import com.mohiva.play.silhouette.api.util.Clock
import models.Receipt
import org.joda.time.{DateTimeZone, LocalDateTime}
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.libs.ws.{EmptyBody, WSClient}
import services.{AuthTokenService, MWSService}
import utils.{Common, Logger, SignatureUtil}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class SendEmailWithNextToken @Inject() (
                                         mailerClient: MailerClient,
                                         system: ActorSystem,
                                         mwsService: MWSService,
                                         config: Configuration,
                                         service: AuthTokenService,
                                         clock: Clock,
                                         ws: WSClient)(implicit ec: ExecutionContext)
  extends Actor with Logger{


//  override def messagesApi = msgApi

  override def receive = {
    case FindNext(template, token, sendMail) =>
      val now = LocalDateTime.now(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss'Z'")
      println(s"time: $now receive findnext: $template, $token")
      var  parameters = Map.empty[String,String]
      parameters += "Action" -> Common.urlEncode(config.get[String]("mws.action.listOrdersByNext"))
      parameters += "MWSAuthToken" -> Common.urlEncode(config.get[String]("mws.MWSAuthToken"))
      parameters += "SellerId" -> Common.urlEncode(config.get[String]("mws.sellerId"))
      parameters += "AWSAccessKeyId" -> Common.urlEncode(config.get[String]("mws.AWSAccessKeyId"))
      parameters += "SignatureMethod" -> Common.urlEncode(config.get[String]("mws.algorithm"))
      parameters += "SignatureVersion" -> Common.urlEncode("2")
      parameters += "Timestamp" -> Common.urlEncode(now)
      parameters += "Version" -> Common.urlEncode(config.get[String]("mws.version"))
      parameters += "NextToken" -> Common.urlEncode(token)
      val url = SignatureUtil.genURL(config.get[String]("mws.serviceUrl"), config.get[String]("mws.secretKey"),
        config.get[String]("mws.algorithm"), parameters)

      ws.url(url).post(EmptyBody).map { response =>
        val node = response.xml \ "ListOrdersByNextTokenResult" \ "Orders" \ "Order"
        val receipts = node.map { n =>
          Receipt(mwsService.RECEIPT_ID.incrementAndGet(),
            (n \ "SellerOrderId").text,
            (n \ "AmazonOrderId").text,
            (n \ "BuyerName").text,
            (n \ "BuyerEmail").text,
            template,
            now
          )
        }
        //处理receipts,过滤条件，以前是否发过
        mwsService.addReceipts(receipts)
        sendMail(receipts map (_.buyerEmail))

        println(s"=============${receipts.size}")
        receipts foreach { r =>
          println(s"name: ${r.buyerName}, email: ${r.buyerEmail}")
        }
        println(s"=============")

        val nextToken = response.xml \ "ListOrdersByNextTokenResult" \ "NextToken"
        //定时任务 60秒后第二次
        if(nextToken.nonEmpty) {
          println(s"token not empty")
          system.scheduler.scheduleOnce(60 seconds, self, FindNext(template, nextToken.text, sendMail))
        } else {
          println(s"token is empty")
        }
      }
    case _ =>
  }
}

case class FindNext(template: Int, token: String, f: Seq[String] => Unit)
object SendEmailWithNextToken {
}
