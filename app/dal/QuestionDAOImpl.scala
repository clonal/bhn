package dal

import javax.inject.Inject

import models.Question
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.{ExecutionContext, Future}

class QuestionDAOImpl @Inject()(implicit ec: ExecutionContext,
                                reactiveMongoApi: ReactiveMongoApi)extends QuestionDAO {
  override def collection = reactiveMongoApi.database.map(_.collection("question"))

  override def addQuestion(question: JsObject): Future[Question] = {
    (question.validate[Question] map { case s => save(s)}).get
  }

  override def addQuestions(data: JsArray) = {
    val f = for(question <- data.value) yield {
      addQuestion(question.as[JsObject])
    }
    Future.sequence(f)
  }
}
