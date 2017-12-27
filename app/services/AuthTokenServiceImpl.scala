package services

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.util.Clock
import dal.AuthTokenDAO
import models.AuthToken
import org.joda.time.DateTimeZone

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

class AuthTokenServiceImpl @Inject() (
                                       authTokenDAO: AuthTokenDAO,
                                       clock: Clock
                                     )(
                                       implicit
                                       ex: ExecutionContext
                                     ) extends AuthTokenService {
  /**
    * Creates a new auth token and saves it in the backing store.
    *
    * @param userID The user ID for which the token should be created.
    * @param expiry The duration a token expires.
    * @return The saved auth token.
    */
  override def create(userID: UUID, expiry: FiniteDuration) = {
    //TODO 时区是否要用标准时间
    val token = AuthToken(UUID.randomUUID(), userID, clock.now.withZone(DateTimeZone.UTC).plusSeconds(expiry.toSeconds.toInt))
    authTokenDAO.save(token)
  }

  /**
    * Validates a token ID.
    *
    * @param id The token ID to validate.
    * @return The token if it's valid, None otherwise.
    */
  override def validate(id: UUID) = authTokenDAO.find(id)

  /**
    * Cleans expired tokens.
    *
    * @return The list of deleted tokens.
    */
  override def clean = {
    authTokenDAO.findExpired(clock.now.withZone(DateTimeZone.UTC)).flatMap { tokens =>
      Future.sequence( tokens.map { token =>
       authTokenDAO.remove(token.id).map(_ => token)
      })
    }
  }
}
