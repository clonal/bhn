package forms

import play.api.libs.json.{Json, Reads, Writes}

case class  ForgotPasswordForm(email: String)

object ForgotPasswordForm {
  implicit val FORGOT_PASSWORD_JSON_READER: Reads[ForgotPasswordForm] = Json.reads[ForgotPasswordForm]
  implicit val FORGOT_PASSWORD_WRITER: Writes[ForgotPasswordForm] = Json.writes[ForgotPasswordForm]
}
