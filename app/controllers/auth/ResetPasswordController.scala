package controllers.auth

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ResetPasswordForm
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.{AuthTokenService, UserService}
import utils.AuthJWTEnvironment

import scala.concurrent.{ExecutionContext, Future}

/**
  * 重设密码
  * @param config
  * @param components
  * @param userService
  * @param silhouette
  * @param authInfoRepository
  * @param system
  * @param passwordHasherRegistry
  * @param ec
  */
class ResetPasswordController @Inject()(
                                         config: Configuration,
                                         components: ControllerComponents,
                                         userService: UserService,
                                         silhouette: Silhouette[AuthJWTEnvironment],
                                         authInfoRepository: AuthInfoRepository,
                                         authTokenService: AuthTokenService,
                                         system: ActorSystem,
                                         passwordHasherRegistry: PasswordHasherRegistry
                                       ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {

  def submit(token: UUID) = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    request.body.validate[ResetPasswordForm].map { data =>
      authTokenService.validate(token).flatMap {
        case Some(authToken) =>
          userService.retrieve(authToken.userID).flatMap{
            case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
              val passwordInfo = passwordHasherRegistry.current.hash(data.password)
              authInfoRepository.update[PasswordInfo](user.loginInfo, passwordInfo).map { _ =>
                Ok(Json.obj("info" -> "password.reset"))
              }
          }
        case None =>
          Future.successful(BadRequest(Json.obj("error" -> Messages("invalid.reset.link"))))
      }
    }.recoverTotal{ error =>
      Future.successful(BadRequest(Json.obj("error" -> Messages("invalid.reset.link"))))
    }
  }

}
