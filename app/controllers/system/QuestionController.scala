package controllers.system

import javax.inject.Inject

import akka.actor.ActorSystem
import models.Question
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.QuestionService

import scala.concurrent.{ExecutionContext, Future}

class QuestionController @Inject()(components: ControllerComponents,
                                    questionService: QuestionService,
                                    system: ActorSystem
                                  ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport {

  def listQuestions = Action.async{
    implicit request =>
      questionService.queryQuestions().map{ list =>
        Ok(JsArray(list.map(x => Json.toJson(x))))
      }
  }

  def deleteQuestion(id: Int) = Action.async{
    implicit request =>
      questionService.findQuestion(id).flatMap{
        case Some(m) => questionService.removeQuestion(id).map(_ => Ok(Json.obj("info" -> "delete complete")))
        case None => Future(BadRequest(Json.obj("error" -> "nothing to delete")))
      }
  }

  def getQuestion(id: Int) = Action.async{
    implicit request =>
      questionService.findQuestion(id).map {
        case Some(m) => Ok(Json.obj("question" -> Json.toJsObject(m)))
        case None => BadRequest(Json.obj("error" -> "wrong question"))
      }
  }

  def addQuestion = Action.async(parse.json) {
    implicit request =>
      val id = questionService.QUESTION_AUTO_ID.incrementAndGet
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      questionService.addQuestion(jsObject).map(m => Ok(Json.toJsObject(m)))
  }

  def editQuestion = Action.async(parse.json) {
    implicit request =>
      val question = request.body.as[Question]
      questionService.updateQuestion(question).map {
        case Some(q) => Ok(Json.obj("info" -> "update completed"))
        case None => BadRequest(Json.obj("error" -> "wrong question"))
      }
  }

}
