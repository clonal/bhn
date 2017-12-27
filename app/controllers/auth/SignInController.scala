package controllers.auth

import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.api.{LoginEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.SignInForm
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsBoolean, Json}
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import services.UserService
import utils.AuthJWTEnvironment

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class SignInController @Inject()(
                                  config: Configuration,
                                  components: ControllerComponents,
                                  silhouette: Silhouette[AuthJWTEnvironment],
                                  credentialsProvider: CredentialsProvider,
                                  system: ActorSystem,
                                  userService: UserService,
                                  mailerClient: MailerClient,
                                  clock: Clock
                                )(
                                  implicit ec: ExecutionContext
                                ) extends AbstractController(components) with I18nSupport {


  def view = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(JsBoolean(true)))
  }


  def authenticate = Action.async(parse.json) { implicit request =>
    request.body.validate[SignInForm].map { data =>
      val credentials = Credentials(data.email, data.password)
      credentialsProvider.authenticate(credentials).flatMap{ loginInfo =>
        userService.retrieve(loginInfo).flatMap{
          case Some(user) if !user.activated =>
            Future.successful(Ok(Json.obj("error" -> "没有激活账号")))
          case Some(user) =>
            val c = config.underlying
            silhouette.env.authenticatorService.create(loginInfo).map {
              case authenticator if data.rememberMe =>
                authenticator.copy(
                  expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                  idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
//                cookieMaxAge = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge")
                  )
              case authenticator => authenticator
            }.flatMap { authenticator =>
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
                println(s"token: $v")
                Future(Ok(Json.obj("token" -> v)))
//                silhouette.env.authenticatorService.embed(v, result)
              }
            }
          case None =>
//            Future.failed(new IdentityNotFoundException("Couldn't find user"))
            Future.successful(BadRequest(Json.obj("error" -> "Couldn't find user")))
        }
      }.recover {
        case e: ProviderException =>
          BadRequest(Json.obj("error" -> "Couldn't find user !!"))
      }
    } recoverTotal { error =>
      Future.successful(BadRequest(Json.obj("error" -> error.toString())))
    }
  }
}
