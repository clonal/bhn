package controllers.auth

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.SignUpForm
import models.User
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsBoolean, Json, Reads}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.{AuthTokenService, UserService}
import utils.{AuthJWTEnvironment, Common}

import scala.concurrent.{ExecutionContext, Future}

/**
  * 注册Controller
  *
  * @param config
  * @param components
  * @param silhouette
  * @param system
  * @param userService
  * @param avatarService
  * @param authTokenService
  * @param authInfoRepository
  * @param mailerClient
  * @param passwordHasherRegistry
  */
class SignUpController @Inject()(
                                  config: Configuration,
                                  components: ControllerComponents,
                                  silhouette: Silhouette[AuthJWTEnvironment],
                                  system: ActorSystem,
                                  userService: UserService,
                                  avatarService: AvatarService,
                                  authTokenService: AuthTokenService,
                                  authInfoRepository: AuthInfoRepository,
                                  mailerClient: MailerClient,
                                  passwordHasherRegistry: PasswordHasherRegistry
                                ) extends AbstractController(components) with I18nSupport {

  implicit val SIGNUP_JSON_READER: Reads[SignUpForm] = Json.reads[SignUpForm]
  implicit val EXECUTION_CONTEXT: ExecutionContext = system.dispatchers.lookup("silhouette.dispatcher")

  val webPort = config.get[Int]("web.port") //前端端口号

  /**
    * 引导“注册”页面
    *
    * @return 是否可以注册
    */
  def view = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(JsBoolean(true)))
  }

  /**
    * 处理“注册”表单
    *
    * @return 注册结果
    */
  def submit = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    request.body.validate[SignUpForm](SIGNUP_JSON_READER).map { data =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          println(s"该用户已经注册")
          val url = Common.webUrl(routes.SignInController.authenticate().url, webPort, request)
          mailerClient.send(Email(
            subject = Messages("email.already.signed.up.subject"),
            from = Messages("email.from"),
            to = Seq(data.email),
            bodyText = Some(views.txt.emails.alreadySignedUp(user, url).body),
            bodyHtml = Some(views.html.emails.alreadySignedUp(user, url).body)
          ))
          Future.successful(Ok(Json.obj("info" -> Messages("sign.up.email.sent", data.email))))
        case None =>
          println(s"用户可以注册")
          val authInfo = passwordHasherRegistry.current.hash(data.password)
          val user = User(
            userID = UUID.randomUUID(),
            loginInfo = loginInfo,
            name = Some(data.name),
            email = Some(data.email),
            avatarURL = None,
            role = data.role,
            activated = false
          )
          for {
            avatar <- avatarService.retrieveURL(data.email)
            user <- userService.save(user.copy(avatarURL = avatar))
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
            authToken <- authTokenService.create(user.userID)
          } yield {
            //            val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL()
            val url = Common.webUrl(controllers.routes.RouteController.activateRoute(authToken.id.toString).url, webPort, request)
            mailerClient.send(Email(
              subject = Messages("email.sign.up.subject"),
              from = Messages("email.from"),
              to = Seq(data.email),
              bodyText = Some(views.txt.emails.signUp(user, url).body),
              bodyHtml = Some(views.html.emails.signUp(user, url).body)
            ))
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
          }
          Future.successful(Ok(Json.obj("info" -> Messages("sign.up.email.sent", data.email))))
      }
    } recoverTotal { error =>
      Future.successful(BadRequest(Json.obj("error" -> error.toString())))
    }
  }
}