package services

import java.util.concurrent.atomic.AtomicInteger

import models.Column
import play.api.libs.json.{JsArray, JsValue}

import scala.concurrent.Future

trait ColumnService {
  def getOrder(value: Int): Int
  val COLUMN_AUTO_ID = new AtomicInteger()

  def fetchColumn(column: Int): Option[Column]
  def fetchChildrenColumn(column: Int): Seq[Column]

  def initColumn(data: JsArray)
  def addColumns(data: JsArray): Future[Seq[Column]]
  def addColumn(column: Column): Future[Column]
  def saveColumn(column: Column): Future[Option[Column]]

  def getColumn(name: String): Future[Option[Column]]
  def getColumn(id: Int): Future[Option[Column]]
  def getColumns(): Future[Seq[Column]]
  def getChildrenColumn(id: Int): Future[Seq[Column]]
  def deleteColumn(id: Int): Future[Option[Column]]
  def deleteColumnByParent(id: Int): Future[Boolean]

  def getLastColumnID(): Future[Option[Int]]
}
