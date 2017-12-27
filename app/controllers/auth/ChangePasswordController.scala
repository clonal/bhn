package controllers.auth

import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import forms.ChangePasswordForm
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import utils.{AuthJWTEnvironment, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

/**
  * 修改密码
  * @param config
  * @param components
  * @param silhouette
  * @param system
  */
class ChangePasswordController  @Inject()(
                                           config: Configuration,
                                           components: ControllerComponents,
                                           credentialsProvider: CredentialsProvider,
                                           silhouette: Silhouette[AuthJWTEnvironment],
                                           authInfoRepository: AuthInfoRepository,
                                           system: ActorSystem,
                                           passwordHasherRegistry: PasswordHasherRegistry
                                         ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {


  def submit = silhouette.SecuredAction(WithProvider[AuthJWTEnvironment#A](
    CredentialsProvider.ID)).async(parse.json) {
    implicit request: SecuredRequest[AuthJWTEnvironment, JsValue] =>
      request.body.validate[ChangePasswordForm].map { data =>
        val credentials = Credentials(request.identity.email.getOrElse(""), data.currentPassword)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val passwordInfo = passwordHasherRegistry.current.hash(data.newPassword)
          authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
            Ok(Json.obj("success" -> Messages("password.changed")))
          }
        }.recover {
          case e: ProviderException =>
            BadRequest(Json.obj("error" -> Messages("current.password.invalid")))
        }
      } recoverTotal { error =>
        Future.successful(BadRequest(Json.obj("error" -> error.toString())))
      }
  }
}
