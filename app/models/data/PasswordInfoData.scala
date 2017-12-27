package models.data // Combinator syntax

case class PasswordInfoData(hasher: String, password: String, salt: Option[String], loginInfoID: Long)

object PasswordInfoData {

  import reactivemongo.bson._

  implicit object PasswordInfoReader extends BSONDocumentReader[PasswordInfoData] {
    def read(bson: BSONDocument): PasswordInfoData = {
      val opt: Option[PasswordInfoData] = for {
        hasher <- bson.getAs[String]("hasher")
        password <- bson.getAs[String]("password")
        loginInfoID <- bson.getAs[BSONNumberLike]("loginInfoID").map(_.toLong)
      } yield new PasswordInfoData(hasher, password, bson.getAs[String]("salt"), loginInfoID)

      opt.get // the person is required (or let throw an exception)
    }
  }

  implicit object PasswordInfoWriter extends BSONDocumentWriter[PasswordInfoData] {
    def write(passwordInfoData: PasswordInfoData): BSONDocument =
      BSONDocument("hasher" -> passwordInfoData.hasher,
        "password" -> passwordInfoData.password,
        "salt" -> passwordInfoData.salt.getOrElse(""),
        "loginInfoID" -> passwordInfoData.loginInfoID)
  }

/*
  val passwordInfoReads: Reads[PasswordInfoData] = (
    (JsPath \ "hasher").read[String] and
      (JsPath \ "password").read[String] and
      (JsPath \ "salt").read[String] and
      (JsPath \ "loginInfoID").read[Long]
    )(PasswordInfoData.apply _)

  val passwordInfoWrites: Writes[PasswordInfoData] = (
    (JsPath \ "hasher").write[String] and
      (JsPath \ "password").write[String] and
      (JsPath \ "salt").write[String] and
      (JsPath \ "loginInfoID").write[Long]
    )(unlift(PasswordInfoData.unapply))

  implicit val passwordInfoFormat: Format[PasswordInfoData] = Format(passwordInfoReads, passwordInfoWrites)
*/

}