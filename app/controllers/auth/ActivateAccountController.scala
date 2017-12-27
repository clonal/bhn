package controllers.auth

import java.net.URLDecoder
import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import reactivemongo.bson.BSONDocument
import services.{AuthTokenService, UserService}
import utils.{AuthJWTEnvironment, Common}

import scala.concurrent.{ExecutionContext, Future}

/**
  * 激活Controller
  * @param components
  * @param silhouette
  * @param system
  * @param userService
  * @param authTokenService
  * @param mailerClient
  * @param ec
  */
class ActivateAccountController @Inject()(
                                           config: Configuration,
                                           components: ControllerComponents,
                                           silhouette: Silhouette[AuthJWTEnvironment],
                                           system: ActorSystem,
                                           userService: UserService,
                                           authTokenService: AuthTokenService,
                                           mailerClient: MailerClient
                                         )(
                                           implicit ec: ExecutionContext
                                         ) extends AbstractController(components) with I18nSupport {

  val webPort = config.get[Int]("web.port") //前端端口号

  /**
    * 激活账号
    * @param token
    * @return
    */
  def activate(token: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.validate(UUID.fromString(token)).flatMap {
      case Some(authToken) =>
        userService.retrieve(authToken.userID).flatMap {
          case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
            userService.update(BSONDocument("userID" -> user.userID.toString),
              BSONDocument("$set" -> BSONDocument("activated" -> true))).map { result =>
              if(result.ok) {
                Ok(Json.obj("result" -> "激活成功"))
              } else {
                Ok(Json.obj("error" -> "激活异常"))
              }
            }
          case _ =>
            Future(Ok(Json.obj("error" -> "激活异常1")))
        }
      case None =>
        Future(Ok(Json.obj("error" -> "不存在的token")))
    }
  }


  /**
    * 手动再次发送激活邮件
    * @param email
    * @return
    */
  def send(email: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)

    userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.userID).map { authToken =>
          val url = Common.webUrl(controllers.routes.RouteController.activateRoute(authToken.id.toString).url, webPort, request)
          mailerClient.send(Email(
            subject = Messages("email.activate.account.subject"),
            from = Messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(views.txt.emails.activateAccount(user, url).body),
            bodyHtml = Some(views.html.emails.activateAccount(user, url).body)
          ))
          Ok(Json.obj("info" -> Messages("sign.up.email.sent", email)))
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("error" -> "不存在的用户或者已经激活")))
    }
  }
}
