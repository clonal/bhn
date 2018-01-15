package controllers.system

import java.io.File
import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{ColumnService, MenuService}

import scala.concurrent.{ExecutionContext, Future}

class ColumnController @Inject()(
                                  config: Configuration,
                                  components: ControllerComponents,
                                  columnService: ColumnService,
                                  system: ActorSystem
                                ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {
  final val IMG_PATH = System.getProperty("user.dir") + config.underlying.getString("play.assets.path") + File.separator + "images"

  def listColumns() = Action.async{
    implicit request =>

      Future(Ok(""))
  }

  def getColumn(name: String) = Action.async{
    implicit request =>
      columnService.getColumn(name).map{
        case Some(c) =>
          Ok(Json.obj("column" -> c))
        case None =>
          BadRequest(Json.obj("error" -> "column not exsits"))
      }
  }

  def getChildrenColumn(id: Int) = Action.async {
    implicit request =>
      columnService.getChildrenColumn(id).map { list =>
        Ok(Json.toJson(list))
      }
  }

}
