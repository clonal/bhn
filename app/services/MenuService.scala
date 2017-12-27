package services

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

import models.Menu
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future


trait MenuService {
  val MENU_AUTO_ID = new AtomicInteger()
  //初始化
  def init(data: JsArray)

  //添加菜单
  def addMenu(data: JsObject): Future[Menu]
  def addMenus(data: JsArray): Future[Seq[Menu]]
  //移除菜单
  def removeMenu(menu: Int): Future[WriteResult]

  //修改菜单
  def updateMenu(menu: Menu): Future[Menu]
  def updateMenu(selector: BSONDocument, modifier: BSONDocument): Future[UpdateWriteResult]
  //查询菜单
  def findMenu(menu: Int): Future[Option[Menu]]
  def findMenuByOrder(parent: Int, order: Int): Future[Option[Menu]]
  def queryMenus(sort: Boolean = true): Future[Seq[Menu]]
  def findChildrenOfMenu(menu: Int): Future[Seq[Menu]]

  //删除菜单图片
  def deleteImage(menu: Int, index: String, path: String): Future[Menu]

  //查询图片名字
  def getImageName(menu: Int, index: String): Future[Option[String]]

  def addImages(menu: Int, img: Seq[String]): Future[Option[Menu]]
  //替换图片
  def replaceImage(menu: Int, index: String, img: String): Future[Option[Menu]]

  def getLastID(): Future[Option[Int]]
}
