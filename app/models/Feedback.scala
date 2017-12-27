package models

import java.util.Date

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Feedback(id: Int,
                    category: Int,
                    market: Int,
                    name: String,
                    email: String,
                    order: String,
                    product: Int,
                    suggest: String,
                    image: String,
                    date: DateTime,
                    ip: String) {
}

object Feedback {

/*  def apply2(id: Int, category: Int, market: Int, name: String, email: String,
            order: String, product: Int, suggest: String, image: String,
            date: String, ip: String): Feedback = {
    Feedback(id, category, market, name, email, order, product, suggest, image,
      DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")), ip)
  }

  def unapply2(arg: Feedback): Option[(Int, Int, Int, String, String, String, Int, String, String, String, String)] = {
      if (arg == null) {
        None
      } else {
        Some(arg.id, arg.category, arg.market, arg.name, arg.email, arg.order, arg.product, arg.suggest,
          arg.image, arg.date.toString("yyyy-MM-dd HH:mm:ss"), arg.ip)
      }
  }*/

  implicit object FeedbackTokenReader extends BSONDocumentReader[Feedback] {
    def read(bson: BSONDocument): Feedback = {
      val opt: Option[Feedback] = for {
        id <- bson.getAs[Int]("id")
        category <- bson.getAs[Int]("category")
        market <- bson.getAs[Int]("market")
        name <- bson.getAs[String]("name")
        email <- bson.getAs[String]("email")
        order <- bson.getAs[String]("order")
        product <- bson.getAs[Int]("product")
        suggest <- bson.getAs[String]("suggest")
        image <- bson.getAs[String]("image")
        date <- bson.getAs[Date]("date")
        ip <- bson.getAs[String]("ip")
      } yield new Feedback(id, category, market, name, email, order, product, suggest,
        image, new DateTime(date), ip)
      opt.get // the person is required (or let throw an exception)
    }
  }

  implicit object FeedbackTokenWriter extends BSONDocumentWriter[Feedback] {
    def write(feedback: Feedback): BSONDocument =
      BSONDocument("id" -> feedback.id,
        "category" -> feedback.category,
        "market" -> feedback.market,
        "name" -> feedback.name,
        "email" -> feedback.email,
        "order" -> feedback.order,
        "product" -> feedback.product,
        "suggest" -> feedback.suggest,
        "image" -> feedback.image,
        "date" -> feedback.date.toDate,
        "ip" -> feedback.ip)
  }

  /*implicit lazy val feedbackReads: Reads[Feedback] = (
    (__ \ "id").read[Int] and
    (__ \ "category").read[Int] and
    (__ \ "market").read[Int] and
    (__ \ "name").read[String] and
    (__ \ "email").read[String] and
    (__ \ "order").read[String] and
    (__ \ "product").read[Int] and
    (__ \ "suggest").read[String] and
    (__ \ "image").read[String] and
    (__ \ "date").read[String] and
    (__ \ "ip").read[String]
    )(Feedback.apply2 _)

  implicit lazy val feedbackWrites: Writes[Feedback] = (
      (__ \ "id").write[Int] and
      (__ \ "category").write[Int] and
      (__ \ "market").write[Int] and
      (__ \ "name").write[String] and
      (__ \ "email").write[String] and
      (__ \ "order").write[String] and
      (__ \ "product").write[Int] and
      (__ \ "suggest").write[String] and
      (__ \ "image").write[String] and
      (__ \ "date").write[String] and
      (__ \ "ip").write[String]
    )(unlift(Feedback.unapply2))*/

  implicit val feedbackFormat: OFormat[Feedback] = Json.format[Feedback]
}


