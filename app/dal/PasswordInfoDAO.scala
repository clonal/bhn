package dal

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.data.{LoginInfoData, PasswordInfoData}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * 操作passwordInfo数据表
  * @param ec
  * @param reactiveMongoApi
  */
class PasswordInfoDAO @Inject() (implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi)
  extends DelegableAuthInfoDAO[PasswordInfo] {

  def loginInfoCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("login_info"))

  def passwordsCollection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("passwordInfo"))

  override def find(loginInfo: LoginInfo) = {
    loginInfoCollection.flatMap(_.find(BSONDocument("providerID" -> loginInfo.providerID,
      "providerKey" -> loginInfo.providerKey)).one[LoginInfoData]).flatMap{
      case Some(lid) =>
          passwordsCollection.flatMap(_.find(BSONDocument("loginInfoID" -> lid.id))
            .one[PasswordInfoData]).map{ op =>
            op.map(data => PasswordInfo(data.hasher, data.password, data.salt))
          }
      case _ =>
        Future(None)
    }
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo) = {
    loginInfoCollection.flatMap(_.find(BSONDocument("providerID" -> loginInfo.providerID,
      "providerKey" -> loginInfo.providerKey)).requireOne[LoginInfoData]).flatMap{ info =>
      passwordsCollection.flatMap(_.insert(PasswordInfoData(authInfo.hasher, authInfo.password,
        authInfo.salt, info.id))).map(_ => authInfo)
    }
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo) = {
    loginInfoCollection.flatMap(_.find(BSONDocument("providerID" -> loginInfo.providerID,
      "providerKey" -> loginInfo.providerKey)).requireOne[LoginInfoData]).flatMap{ info =>
      passwordsCollection.flatMap(_.update(BSONDocument("loginInfoID" -> info.id),
        BSONDocument("$set" -> BSONDocument("hasher" -> authInfo.hasher, "password" -> authInfo.password, "salt" -> authInfo.salt))))
        .map(_ => authInfo)
    }
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo) = {
    find(loginInfo).flatMap {
      case Some(info) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  override def remove(loginInfo: LoginInfo) = {
    loginInfoCollection.flatMap(_.find(BSONDocument("providerID" -> loginInfo.providerID,
      "providerKey" -> loginInfo.providerKey)).requireOne[LoginInfoData]).flatMap{   info =>
      passwordsCollection.flatMap(_.remove(BSONDocument("loginInfoID" -> info.id))).map(_ => ())
    }
  }
}
