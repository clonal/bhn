import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONString}

package object models {
  implicit object MapWriter extends BSONDocumentWriter[Map[String,String]] {
    def write(map: Map[String, String]): BSONDocument =
      BSONDocument(map.map(x => (x._1, BSONString(x._2))))
  }

  implicit val dateWrites = new Writes[DateTime] {
    def writes(o: DateTime): JsValue = {
      Json.toJson(o.toString("yyyy-MM-dd HH:mm:ss"))
    }
  }

  implicit val dateReads = new Reads[DateTime] {
    override def reads(json: JsValue) = {
      json match {
        case v: JsString =>
          JsSuccess(DateTime.parse(v.value, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
        case _ => JsError("not valid date str!")
      }
    }
  }
}
