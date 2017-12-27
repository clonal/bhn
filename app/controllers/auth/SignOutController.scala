package controllers.auth

import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import utils.AuthJWTEnvironment

/**
  * 登出
  * @param config
  * @param components
  * @param silhouette
  * @param system
  */
class SignOutController @Inject()(
                                   config: Configuration,
                                   components: ControllerComponents,
                                   silhouette: Silhouette[AuthJWTEnvironment],
                                   system: ActorSystem
                                 ) extends AbstractController(components) with I18nSupport {

  def signOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[AuthJWTEnvironment, AnyContent] =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok(Json.obj("msg" -> "log out")))
  }
}
