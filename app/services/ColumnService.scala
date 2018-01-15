package services

import java.util.concurrent.atomic.AtomicInteger

import models.Column
import play.api.libs.json.JsArray

import scala.concurrent.Future

trait ColumnService {


  val COLUMN_AUTO_ID = new AtomicInteger()

  def initColumn(data: JsArray)
  def addColumns(data: JsArray): Future[Seq[Column]]

  def getColumn(name: String): Future[Option[Column]]
  def getChildrenColumn(id: Int): Future[Seq[Column]]

  def getLastColumnID(): Future[Option[Int]]
}
