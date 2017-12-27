package models.data

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONNumberLike} // Combinator syntax

case class UserLoginInfoData(userID: String, loginInfoID: Long)

object UserLoginInfoData {

//  implicit val userLoginInfoHandler: BSONHandler[BSONDocument, UserLoginInfoData] = Macros.handler[UserLoginInfoData]

  implicit object UserLoginInfoReader extends BSONDocumentReader[UserLoginInfoData] {
    def read(bson: BSONDocument): UserLoginInfoData = {
      val opt: Option[UserLoginInfoData] = for {
        userID <- bson.getAs[String]("userID")
        loginInfoID <- bson.getAs[BSONNumberLike]("loginInfoID").map(_.toLong)
      } yield new UserLoginInfoData(userID, loginInfoID)

      opt.get // the person is required (or let throw an exception)
    }
  }

  implicit object UserLoginInfoWriter extends BSONDocumentWriter[UserLoginInfoData] {
    def write(userLoginInfo: UserLoginInfoData): BSONDocument =
      BSONDocument("userID" -> userLoginInfo.userID,
        "loginInfoID" -> userLoginInfo.loginInfoID)
  }

 /* val userLoginInfoReads: Reads[UserLoginInfoData] = (
    (JsPath \ "userID").read[String] and
      (JsPath \ "loginInfoID").read[String]
    )(UserLoginInfoData.apply _)

  val userLoginInfoWrites: Writes[UserLoginInfoData] = (
    (JsPath \ "userID").write[String] and
      (JsPath \ "loginInfoID").write[String]
    )(unlift(UserLoginInfoData.unapply))

  implicit val userLoginInfoFormat: Format[UserLoginInfoData] = Format(userLoginInfoReads, userLoginInfoWrites)*/
}
