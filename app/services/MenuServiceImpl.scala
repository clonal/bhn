package services
import java.io.File
import javax.inject.{Inject, Singleton}

import dal.MenuDAO
import models.Menu
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument
import utils.FileUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MenuServiceImpl @Inject()(dao: MenuDAO)
                               (implicit ex: ExecutionContext) extends MenuService{

  override def init(data: JsArray) = {
    dao.isEmpty.flatMap{
      case true => addMenus(data)
    } andThen { case _ =>
      getLastID().foreach{
        case Some(i) =>
          MENU_AUTO_ID.set(i)
          println(s"value2: $MENU_AUTO_ID")
      }
    }
  }

  override def addMenu(data: JsObject) = {
    dao.addMenu(data)
  }

  override def addMenus(data: JsArray) = {
    dao.addMenus(data)
  }

  override def removeMenu(menu: Int): Future[WriteResult] = {
    //删除子菜单
    findChildrenOfMenu(menu).flatMap { x =>
      val f = Future.sequence((x.map(_.id) :+ menu).map(c =>
        if (c == menu)
          dao.remove(c)
        else
          removeMenu(c)
      ))
      f.map(_.last)
    }
  }


  override def updateMenu(menu: Menu): Future[Menu] = {
    dao.update(menu).map(_ => menu)
  }

  override def updateMenu(selector: BSONDocument, modifier: BSONDocument) = {
    dao.update(selector, BSONDocument("$set" -> modifier))
  }

  override def findMenu(menu: Int): Future[Option[Menu]] = {
    dao.find[Menu](menu)
  }

  override def findMenuByOrder(parent: Int, order: Int) = {
    dao.findByOrder(parent, order)
  }

  /**
    * 列出菜单
    *
    * @return
    */
  override def queryMenus(sort: Boolean = true) = {
    //TODO 树排序
    dao.findAll[Menu]
  }


  override def findChildrenOfMenu(menu: Int) = {
    dao.findChildrenOfMenu(menu)
  }

  /**
    * 删除菜单指定图片
    *
    * @param menu
    * @param index
    * @return
    */
  override def deleteImage(menu: Int, index: String, path: String) = {
    dao.find[Menu](menu).flatMap {
      case Some(m) =>
        m.banner.get(index) foreach { name =>
          val file = new File(path + name)
          FileUtil.deleteFile(file)
        }
        val banner = m.banner.filterNot(_._1 == index)
        val newMenu = m.copy(banner = banner)
        dao.update(newMenu).map(_ => newMenu)
      case None =>
        Future.failed(new Exception("no menu found!"))
    }
  }

  /**
    * 查询菜单图片
    * @param menu
    * @param index
    * @return
    */
  override def getImageName(menu: Int, index: String) = {
    dao.find[Menu](menu).map{
      case Some(m) => m.banner.get(index)
      case None => None
    }
  }


  override def addImages(menu: Int, imgs: Seq[String]) = {
    dao.find[Menu](menu).flatMap {
      case Some(m) =>
        var index = if (m.banner.nonEmpty) m.banner.maxBy(_._1)._1.toInt else 0
        val banner = imgs.foldLeft(m.banner){(m, s) =>
          index+=1
          m.updated(index.toString, s)
        }
        val newMenu = m.copy(banner = banner)
        dao.update(newMenu).map(_ => Some(newMenu))
      case None =>
        Future(None)
    }
  }

  /**
    * 替换图片
    *
    * @param menu
    * @param index
    * @param img
    * @return
    */
  override def replaceImage(menu: Int, index: String, img: String) = {
    dao.find[Menu](menu).flatMap {
      case Some(m) =>
        val newMenu = m.copy(banner = m.banner.updated(index, img))
        dao.update(newMenu).map(_ => Some(newMenu))
      case None =>
        Future(None)
    }
  }

  override def getLastID() = {
    dao.getLastID
  }
}
