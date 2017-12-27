package controllers.auth

import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ForgotPasswordForm
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{AuthTokenService, UserService}
import utils.{AuthJWTEnvironment, Common}

import scala.concurrent.{ExecutionContext, Future}

/**
  * 忘记密码
  * @param config
  * @param components
  * @param credentialsProvider
  * @param userService
  * @param silhouette
  * @param authInfoRepository
  * @param authTokenService
  * @param system
  * @param mailerClient
  * @param ec
  */
class ForgotPasswordController  @Inject()(
                                           config: Configuration,
                                           components: ControllerComponents,
                                           credentialsProvider: CredentialsProvider,
                                           userService: UserService,
                                           silhouette: Silhouette[AuthJWTEnvironment],
                                           authInfoRepository: AuthInfoRepository,
                                           authTokenService: AuthTokenService,
                                           system: ActorSystem,
                                           mailerClient: MailerClient
                                         ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {

    val webPort = config.get[Int]("web.port") //前端端口号

    def submit = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
      request.body.validate[ForgotPasswordForm].map { data =>
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
//        val result = Redirect(routes.SignInController.view()).flashing("info" -> Messages("reset.email.sent"))
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if user.email.isDefined =>
            authTokenService.create(user.userID).map { authToken =>
              val url = Common.webUrl(controllers.routes.RouteController.resetPasswordRoute(authToken.id.toString).url, webPort, request)
              mailerClient.send(Email(
                subject = Messages("email.reset.password.subject"),
                from = Messages("email.from"),
                to = Seq(data.email),
                bodyText = Some(views.txt.emails.resetPassword(user, url).body),
                bodyHtml = Some(views.html.emails.resetPassword(user, url).body)
              ))
              Ok(Json.obj("info" -> Messages("reset.email.sent")))
            }
          case None => Future.successful(BadRequest(Json.obj("info" -> Messages("reset.email.notFound"))))
        }
      } recoverTotal( error =>
        Future.successful(BadRequest(Json.obj("error" -> error.toString())))
      )
    }
}
