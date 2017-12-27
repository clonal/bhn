package dal

import models.Feedback
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.Future

trait FeedbackDAO extends BaseDAO{
  def findAndRemoveFeedback(id: Int):Future[Option[Feedback]]
  def addFeedback(data: JsObject): Future[Feedback]
  def addFeedbacks(data: JsArray): Future[Seq[Feedback]]
}
