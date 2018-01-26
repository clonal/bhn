package dal

import javax.inject.Inject

import models.Feedback
import play.api.libs.json.{JsArray, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

class FeedBackDAOImpl @Inject()(implicit ec: ExecutionContext,
                                reactiveMongoApi: ReactiveMongoApi) extends FeedbackDAO {
  override def collection = reactiveMongoApi.database.map(_.collection("feedback"))

  override def addFeedback(data: JsObject) = {
    save(data.as[Feedback])
//    (data.validate[Feedback] map { case s => save(s)}).get
  }

  override def addFeedbacks(data: JsArray) = {
    val f = for(feedback <- data.value) yield {
      addFeedback(feedback.as[JsObject])
    }
    Future.sequence(f)
  }

  override def findAndRemoveFeedback(id: Int) = {
    collection.flatMap(_.findAndRemove(BSONDocument("id" -> id)).map(_.result[Feedback]))
  }
}
