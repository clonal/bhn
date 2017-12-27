package dal

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.data.{LoginInfoData, UserLoginInfoData}
import models.User
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * 操作user数据表
  *
  * @param ec
  * @param reactiveMongoApi
  */
class UserDAOImpl @Inject()(autoIncDAO: AutoIncDAO)(implicit ec: ExecutionContext,
                                                    reactiveMongoApi: ReactiveMongoApi) extends UserDAO {

  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("user"))

  def loginInfoCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("login_info"))

  def userLoginCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("user_login"))

  def loginInfoQuery(loginInfo: LoginInfo) = {
    val query = BSONDocument("providerID" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey)
    loginInfoCollection.flatMap { c =>
      println(s"查找 ${loginInfo.providerID} ,${loginInfo.providerKey} 的用户")
      c.find(query).one[LoginInfoData]
    }
  }

  def saveUser(user: User) = {
    collection.flatMap(_.insert(user))
    val query = BSONDocument("providerID" -> user.loginInfo.providerID, "providerKey" -> user.loginInfo.providerKey)
    val loginQueryAction = loginInfoQuery(user.loginInfo)

    val loginInfoAction = for (action <- loginQueryAction) yield {
      action match {
        case Some(info) =>
          Future.successful(info)
        case None =>
          println(s"插入loginInfo数据")
          autoIncDAO.getNextSeq.map {
            case Some(v) => v.seq
            case None => 1
          }.flatMap{ seq =>
            println(s"获取seq为 $seq")
            loginInfoCollection.flatMap(_.insert(BSONDocument("id" -> seq,
              "providerID" -> user.loginInfo.providerID, "providerKey" -> user.loginInfo.providerKey)))
          }
//          loginInfoCollection.flatMap(_.insert(query))
      }
    }

    //TODO 测试失败情况
    val action = for {
      _ <- collection.flatMap(_.update(BSONDocument("email" -> user.email), BSONDocument("$set" -> query)))
      _ <- loginInfoAction.flatMap(x => x)
      loginInfo <- loginInfoCollection.flatMap { c => c.find(query).requireOne[LoginInfoData] }
      result <- userLoginCollection.flatMap(_.insert(BSONDocument("userID" -> user.userID.toString, "loginInfoID" -> loginInfo.id)))
    } yield result
    action.map(_ => user)
  }

  override def find(loginInfo: LoginInfo) = {
    loginInfoQuery(loginInfo).flatMap {
      case Some(data) =>
        println(s"查到 存在${loginInfo.providerID} ,${loginInfo.providerKey} 的用户")
        for {
          dbUserLoginInfo <- userLoginCollection.flatMap(_.find(BSONDocument("loginInfoID" -> data.id)).requireOne[UserLoginInfoData])
          dbUser <- collection.flatMap(_.find(BSONDocument("userID" -> dbUserLoginInfo.userID)).one[User])
        } yield dbUser
      case None =>
        println(s"不存在${loginInfo.providerID} ,${loginInfo.providerKey} 的用户")
        Future.successful(None)
    }
  }

  override def find(id: UUID) = {
    collection.flatMap(_.find(BSONDocument("userID" -> id.toString)).one[User])
  }

//  override def update(selector: BSONDocument, modifier: BSONDocument) = {
//    collection.flatMap(_.update(selector, modifier))
//  }

}

