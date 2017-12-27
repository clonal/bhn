package forms

import play.api.libs.json.{Json, Reads, Writes}

case class ResetPasswordForm(password: String)

object ResetPasswordForm {
  implicit val RESET_PASSWORD_JSON_READER: Reads[ResetPasswordForm] = Json.reads[ResetPasswordForm]
  implicit val RESET_PASSWORD_WRITER: Writes[ResetPasswordForm] = Json.writes[ResetPasswordForm]
}