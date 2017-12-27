package utils

import java.net.URLEncoder
import java.text.SimpleDateFormat
import javax.inject.Singleton

import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import play.api.mvc.RequestHeader

@Singleton
object Common {
  final val DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  final val CharacterEncoding = "UTF-8"

  def webUrl(url: String, port: Int, request: RequestHeader) = {
    val portStr = if (port == 80) "" else ":" + port
    "http" + (if (request.secure) "s" else "") + "://" + request.domain + portStr + url
  }

  /**
    * 通过json组成修改的BSONDocument
    * @param jsValue
    * @param key
    * @return
    */
  /*def bsonDocumentModifier(jsValue: JsValue, key: String) = {
    var doc = BSONDocument()
    (jsValue \ "parent").asOpt.foreach{v =>
      doc ++= BSONDocument("parent" -> v)
    }
    doc
  }*/

  def urlEncode(rawValue: String): String =  {
    URLEncoder.encode(rawValue, CharacterEncoding)
      .replace("+", "%20")
      .replace("*", "%2A")
      .replace("%7E","~")
  }

  def timeFormat(time: String): String = {
    val format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    LocalDateTime.parse(time, format).toString("yyyy-MM-dd'T'HH:mm:ss'Z'")
  }
}
