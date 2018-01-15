package services

import javax.inject.{Inject, Singleton}

import dal.ColumnDAO
import models.Column
import play.api.libs.json.JsArray

import scala.concurrent.ExecutionContext

@Singleton
class ColumnServiceImpl @Inject()(columnDAO: ColumnDAO)(implicit ex: ExecutionContext)extends ColumnService {
  final var columns: Map[Int, Column] = Map.empty


  override def addColumns(data: JsArray) = {
    columnDAO.addColumns(data)
  }


  override def getLastColumnID() = {
    columnDAO.getLastID
  }

  override def initColumn(data: JsArray): Unit = {
    columnDAO.isEmpty.flatMap{
      case true => addColumns(data)
    } andThen { case _ =>
      getLastColumnID().foreach{
        case Some(i) => COLUMN_AUTO_ID.set(i)
      }
      columnDAO.findAll[Column].foreach { s =>
        columns = s.map(x => x.id -> x).toMap
      }
    }
  }

  override def getColumn(name: String) = {
    columnDAO.getColumn(name)
  }

  override def getChildrenColumn(id: Int) = {
    columnDAO.getChildrenColumn(id)
  }
}
