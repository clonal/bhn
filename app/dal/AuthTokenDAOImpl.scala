package dal

import java.util.UUID
import javax.inject.Inject

import models.AuthToken
import org.joda.time.DateTime
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class AuthTokenDAOImpl @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) extends AuthTokenDAO{

  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("auth_token"))

  /**
    * Finds a token by its ID.
    *
    * @param id The unique token ID.
    * @return The found token or None if no token for the given ID could be found.
    */
  override def find(id: UUID) = {
    collection.flatMap(_.find(BSONDocument("id" -> id.toString)).one[AuthToken])
  }

  /**
    * Finds expired tokens.
    *
    * @param dateTime The current date time.
    */
  override def findExpired(dateTime: DateTime) = {
    collection.flatMap(_.find(BSONDocument(
      "expiry" -> BSONDocument("$lt" -> dateTime.toDate())))
      .cursor[AuthToken]().collect[List]())
  }

  /**
    * Saves a token.
    *
    * @param token The token to save.
    * @return The saved token.
    */
  override def save(token: AuthToken) = {
    collection.flatMap(_.insert(token)).map(_ => token)
  }

  /**
    * Removes the token for the given ID.
    *
    * @param id The ID for which the token should be removed.
    * @return A future to wait for the process to be completed.
    */
  override def remove(id: UUID) = {
    collection.flatMap(_.remove(BSONDocument("id" -> id.toString))).map(_ => ())
  }

}
