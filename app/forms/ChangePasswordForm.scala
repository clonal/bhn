package forms

import play.api.libs.json.{Json, Reads, Writes}

case class ChangePasswordForm(currentPassword: String, newPassword: String)

object ChangePasswordForm {

  implicit val CHANGE_PASSWORD_JSON_READER: Reads[ChangePasswordForm] = Json.reads[ChangePasswordForm]
  implicit val CHANGE_PASSWORD_WRITER: Writes[ChangePasswordForm] = Json.writes[ChangePasswordForm]
}