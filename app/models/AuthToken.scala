package models

import java.util.{Date, UUID}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import utils.Common

case class AuthToken(id: UUID, userID: UUID, expiry: DateTime)

object AuthToken {
  implicit object authTokenReader extends BSONDocumentReader[AuthToken] {
    def read(bson: BSONDocument): AuthToken = {
      val opt: Option[AuthToken] = for {
        id <- bson.getAs[String]("id")
        userID <- bson.getAs[String]("userID")
        expiry <- bson.getAs[Date]("expiry")
      } yield new AuthToken(UUID.fromString(id), UUID.fromString(userID),
//        new DateTime(Common.DateFormat.parse(expiry)))
       new DateTime(expiry))

      opt.get // the person is required (or let throw an exception)
    }
  }

  implicit object authTokenWriter extends BSONDocumentWriter[AuthToken] {
    def write(authToken: AuthToken): BSONDocument =
      BSONDocument("id" -> authToken.id.toString,
        "userID" -> authToken.userID.toString,
//        "expiry" -> authToken.expiry.toString("yyyy-MM-dd HH:mm:ss"))
        "expiry" -> authToken.expiry.toDate)
  }
}
