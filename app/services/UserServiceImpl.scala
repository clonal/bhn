package services
import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import dal.UserDAO
import models.User
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext

class UserServiceImpl @Inject()(dao: UserDAO)(implicit ex: ExecutionContext) extends UserService{
  /**
    * Retrieves a user that matches the specified ID.
    *
    * @param id The ID to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given ID.
    */
  override def retrieve(id: UUID) = dao.find(id)

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  override def save(user: User) = {
    dao.saveUser(user)
  }

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  override def save(profile: CommonSocialProfile) = ???

  override def retrieve(loginInfo: LoginInfo) = dao.find(loginInfo)

  override def update(selector: BSONDocument, modifier: BSONDocument) = dao.update(selector, modifier)
}
