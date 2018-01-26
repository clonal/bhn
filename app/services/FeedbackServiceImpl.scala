package services

import javax.inject.{Inject, Singleton}

import dal.FeedbackDAO
import models.Feedback
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeedbackServiceImpl @Inject()(feedbackDAO: FeedbackDAO)(implicit ex: ExecutionContext)extends FeedbackService {

  def getLastFeedbackID(): Future[Option[Int]] = {
    feedbackDAO.getLastID
  }

  override def findAndRemoveFeedback(id: Int) = {
    feedbackDAO.findAndRemoveFeedback(id)
  }

  override def findFeedback(id: Int) = {
    feedbackDAO.findOne[Feedback](id)
  }

  override def queryFeedbacks() = {
    feedbackDAO.findAll[Feedback]
  }

  override def addFeedback(data: JsObject) = {
    feedbackDAO.addFeedback(data)
  }

  def addFeedbacks(data: JsArray): Future[Seq[Feedback]] = {
    feedbackDAO.addFeedbacks(data)
  }

  override def initFeedback(data: JsArray): Unit = {
    feedbackDAO.isEmpty.flatMap{
      case true => addFeedbacks(data)
    } andThen { case _ =>
      getLastFeedbackID().foreach{
        case Some(i) => FEEDBACK_AUTO_ID.set(i)
      }
    }
  }
}
