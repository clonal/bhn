package services

import javax.inject.{Inject, Singleton}

import dal.QuestionDAO
import models.Question
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class QuestionServiceImpl @Inject()(questionDAO: QuestionDAO )
                                   (implicit ex: ExecutionContext)extends QuestionService {


  override def findQuestion(id: Int) = {
    questionDAO.find[Question](id)
  }

  override def queryQuestions() = {
    questionDAO.findAll[Question]
  }

  override def addQuestion(data: JsObject): Future[Question] = {
    questionDAO.addQuestion(data)
  }

  override def addQuestions(data: JsArray) = {
    questionDAO.addQuestions(data)
  }

  def getLastQuestionID(): Future[Option[Int]] = {
    questionDAO.getLastID
  }

  override def removeQuestion(id: Int) = {
    questionDAO.remove(id)
  }

  override def updateQuestion(question: Question) = {
    questionDAO.update(BSONDocument("id" -> question.id), question)
  }

  override def initQuestion(data: JsArray): Unit = {
    questionDAO.isEmpty.flatMap{
      case true => addQuestions(data)
    } andThen { case _ =>
      getLastQuestionID().foreach{
        case Some(i) => QUESTION_AUTO_ID.set(i)
      }
    }
  }
}
