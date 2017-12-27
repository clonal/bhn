package models.data

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter} // Combinator syntax

case class LoginInfoData (id: Long, providerID: String, providerKey: String)

object LoginInfoData {

  implicit object LoginInfoReader extends BSONDocumentReader[LoginInfoData] {
    def read(bson: BSONDocument): LoginInfoData = {
      val opt: Option[LoginInfoData] = for {
        id <- bson.getAs[Long]("id")
        providerID <- bson.getAs[String]("providerID")
        providerKey <- bson.getAs[String]("providerKey")
      } yield new LoginInfoData(id, providerID, providerKey)

      opt.get // the person is required (or let throw an exception)
    }
  }

  implicit object LoginInfoWriter extends BSONDocumentWriter[LoginInfoData] {
    def write(loginInfoData: LoginInfoData): BSONDocument =
      BSONDocument("id" -> loginInfoData.id,
        "providerID" -> loginInfoData.providerID,
        "providerKey" -> loginInfoData.providerKey)
  }

  /*val loginInfoReads: Reads[LoginInfoData] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "providerID").read[String] and
      (JsPath \ "providerKey").read[String]
  )(LoginInfoData.apply _)

  val loginInfoWrites: Writes[LoginInfoData] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "providerID").write[String] and
      (JsPath \ "providerKey").write[String]
  )(unlift(LoginInfoData.unapply))

  implicit val loginInfoFormat: Format[LoginInfoData] = Format(loginInfoReads, loginInfoWrites)*/
//
//  implicit val loginInfoFormat: OFormat[LoginInfoData] = Json.format[LoginInfoData]
//  implicit val loginInfoWrite: OWrites[LoginInfoData] = Json.writes[LoginInfoData]
}
