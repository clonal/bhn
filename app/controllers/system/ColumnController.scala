package controllers.system

import java.io.File
import javax.inject.Inject

import akka.actor.ActorSystem
import models.{Column, TreeNode}
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ColumnService

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
      columnService.getColumns().map { l =>
        val (parents, children) = l.partition(_.parent == 0)
        val pNodes = parents.map { cl =>
          new TreeNode(cl.id, cl.name, cl.order, cl.desc, Seq.empty)
        }
        children foreach { c =>
          pNodes.find(_.id == c.parent).foreach (_.addChild(new TreeNode(c.id, c.name, c.order, c.desc, Seq.empty)))
        }
        Ok(Json.toJson(pNodes))
      }
  }

  def getColumn(id: Int) = Action.async {
    implicit request =>
      columnService.getColumn(id).map{
        case Some(c) =>
          Ok(Json.obj("column" -> c))
        case None =>
          BadRequest(Json.obj("error" -> "column not exsits"))
      }
  }

  def getColumnByName(name: String) = Action.async{
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

  def deleteColumn(id: Int) = Action.async {
    implicit request =>
      columnService.deleteColumn(id).flatMap {
        case Some(c) =>
          columnService.deleteColumnByParent(id)
            .map(_ => Ok(Json.obj("info" -> s"删除${c.desc}栏目成功")))
        case None =>
          Future(Ok(Json.obj("info" -> s"无此栏目")))
      }
  }

  def saveColumn() = Action.async(parse.json) {
    implicit request =>
      var json = request.body.as[JsObject]
      if (json("id").as[Int] == 0) {
        json ++= Json.obj("id" -> columnService.COLUMN_AUTO_ID.incrementAndGet())
        json ++= Json.obj("order" -> columnService.getOrder(json("parent").as[Int]))
      }
      val column = json.as[Column]
      columnService.saveColumn(column).map { case _ =>
        Ok(Json.obj("info" -> "add column success!"))
      }
  }
}
