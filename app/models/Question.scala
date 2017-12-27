package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Question(id: Int, question: String, answer: String) {
}

object Question {
  implicit object QuestionReader extends BSONDocumentReader[Question] {
    def read(bson: BSONDocument): Question = {
      val opt: Option[Question] = for {
        id <- bson.getAs[Int]("id")
        question <- bson.getAs[String]("question")
        answer <- bson.getAs[String]("answer")
      } yield {
        new Question(id, question, answer)
      }
      opt.get
    }
  }

  implicit object QuestionWriter extends BSONDocumentWriter[Question] {
    def write(question: Question): BSONDocument =
      BSONDocument("id" -> question.id,
        "question" -> question.question,
        "answer" -> question.answer)
  }

  implicit val questionFormat: OFormat[Question] = Json.format[Question]
}


