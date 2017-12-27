package utils

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{SecuredUser, User}


trait AuthJWTEnvironment extends Env {
//    type I = SecuredUser
    type I = User
    type A = JWTAuthenticator
}