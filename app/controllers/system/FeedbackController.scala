package controllers.system

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{FeedbackService, QuestionService}

import scala.concurrent.{ExecutionContext, Future}

class FeedbackController @Inject()(components: ControllerComponents,
                                   feedbackService: FeedbackService,
                                    system: ActorSystem
                                  )(implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport  {

  def listFeedbacks = Action.async {
    implicit request =>
      feedbackService.queryFeedbacks().map{ list =>
        Ok(JsArray(list.map(x => Json.toJson(x))))
      }
  }

  def deleteFeedback(id: Int) = Action.async {
    implicit request =>
      feedbackService.findAndRemoveFeedback(id).map{
        case Some(feedback) => Ok(Json.obj("info" -> s"delete ${feedback.id} complete ${feedback.name}"))
        case None => BadRequest(Json.obj("error" -> "nothing to delete"))
      }
  }

  def addFeedback() = Action.async(parse.json) {
    implicit request =>
      val id = feedbackService.FEEDBACK_AUTO_ID.incrementAndGet()
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      feedbackService.addFeedback(jsObject).map(_ => Ok(Json.obj("success" -> id)))
  }
}
