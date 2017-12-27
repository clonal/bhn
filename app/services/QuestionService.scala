package services

import java.util.concurrent.atomic.AtomicInteger

import models.Question
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

trait QuestionService {
  val QUESTION_AUTO_ID = new AtomicInteger()

  def updateQuestion(question: Question): Future[Option[Question]]
  def findQuestion(id: Int): Future[Option[Question]]
  def removeQuestion(id: Int): Future[WriteResult]
  def queryQuestions(): Future[Seq[Question]]
  def getLastQuestionID(): Future[Option[Int]]
  def addQuestion(data: JsObject): Future[Question]
  def addQuestions(data: JsArray): Future[Seq[Question]]
  def initQuestion(data: JsArray): Unit

}
