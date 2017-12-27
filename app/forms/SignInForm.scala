package forms

import play.api.libs.json.{Json, Reads, Writes}

case class SignInForm (email: String, password: String, rememberMe: Boolean)

object SignInForm {
  implicit val SIGNIN_JSON_READER: Reads[SignInForm] = Json.reads[SignInForm]
  implicit val SIGNIN_JSON_WRITER: Writes[SignInForm] = Json.writes[SignInForm]
}