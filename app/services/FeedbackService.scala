package services

import java.util.concurrent.atomic.AtomicInteger

import models.Feedback
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

trait FeedbackService {

  val FEEDBACK_AUTO_ID = new AtomicInteger()

  def getLastFeedbackID(): Future[Option[Int]]

  def findAndRemoveFeedback(id: Int): Future[Option[Feedback]]
  def findFeedback(id: Int): Future[Option[Feedback]]
  def queryFeedbacks(): Future[Seq[Feedback]]
  def initFeedback(data: JsArray): Unit
  def addFeedback(data: JsObject): Future[Feedback]
  def addFeedbacks(data: JsArray): Future[Seq[Feedback]]

}
