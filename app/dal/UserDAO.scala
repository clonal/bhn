package dal

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait UserDAO extends BaseDAO{

  def saveUser(user: User): Future[User]

  def find(loginInfo: LoginInfo): Future[Option[User]]

  def find(id: UUID): Future[Option[User]]

//  def update(selector: BSONDocument, modifier: BSONDocument): Future[WriteResult]
}
