package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONNumberLike}

/**
  * The user object.
  *
  * @param userID The unique ID of the user.
  * @param loginInfo The linked login info.
  * @param name Maybe the name of the authenticated user.
  * @param email Maybe the email of the authenticated provider.
  * @param avatarURL Maybe the avatar URL of the authenticated provider.
  * @param role the type of user
  * @param activated Indicates that the user has activated its registration.
  */
case class User(
                 userID: UUID,
                 loginInfo: LoginInfo,
                 name: Option[String],
                 email: Option[String],
                 avatarURL: Option[String],
                 role: Int,
                 activated: Boolean) extends Identity {
}

object User {
//  implicit val userHandler: BSONHandler[BSONDocument, User] = Macros.handler[User]

  implicit object userReader extends BSONDocumentReader[User] {
    def read(bson: BSONDocument): User = {
      val opt: Option[User] = for {
        userID <- bson.getAs[String]("userID")
        loginInfoData <- bson.getAs[BSONDocument]("loginInfo")
        name <- bson.getAs[String]("name")
        email <- bson.getAs[String]("email")
        avatarURL <- bson.getAs[String]("avatarURL")
        role <- bson.getAs[BSONNumberLike]("role").map(_.toInt)
        activated <- bson.getAs[Boolean]("activated")
      } yield {
        val providerID = loginInfoData.getAs[String]("providerID").getOrElse("")
        val providerKey = loginInfoData.getAs[String]("providerKey").getOrElse("")
        new User(UUID.fromString(userID), LoginInfo(providerID, providerKey),
          Some(name), Some(email), Some(avatarURL), role, activated)
      }

      opt.get // the person is required (or let throw an exception)
    }
  }

  implicit object userWriter extends BSONDocumentWriter[User] {
    def write(user: User): BSONDocument =
      BSONDocument("userID" -> user.userID.toString,
        "loginInfo" -> BSONDocument("providerID" -> user.loginInfo.providerID,
          "providerKey" -> user.loginInfo.providerKey),
        "name" -> user.name.getOrElse(""),
        "email" -> user.email.getOrElse(""),
        "avatarURL" -> user.avatarURL.getOrElse(""),
        "role" -> user.role,
        "activated" -> user.activated)
  }
/*  implicit val userFormat: OFormat[User] = Json.format[User]
  implicit val userWrite: OWrites[User] = Json.writes[User]*/
}
