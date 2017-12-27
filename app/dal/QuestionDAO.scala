package dal

import models.Question
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.Future

trait QuestionDAO extends BaseDAO{
  def addQuestions(data: JsArray): Future[scala.Seq[Question]]

  def addQuestion(data: JsObject): Future[Question]

}
