package dal

import models.Menu
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait MenuDAO extends BaseDAO{
//  def save(menu: Menu): Future[Menu]

  def addMenu(data: JsObject): Future[Menu]

  def addMenus(data: JsArray): Future[Seq[Menu]]

//  def find(id: Int): Future[Option[Menu]]

  def find(name: String): Future[Option[Menu]]

  def findByOrder(parent: Int, order: Int): Future[Option[Menu]]

//  def findAll(): Future[Seq[Menu]]

  def findChildrenOfMenu(menu: Int): Future[Seq[Menu]]

  def update(menu: Menu): Future[UpdateWriteResult]

//  def update(selector: BSONDocument, modifier: BSONDocument): Future[UpdateWriteResult]

//  def remove(id: Int): Future[WriteResult]

  def remove(name: String): Future[WriteResult]

//  def getLastID(): Future[Option[Int]]
}
