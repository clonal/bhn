package services

import javax.inject.{Inject, Singleton}

import dal.ColumnDAO
import models.Column
import play.api.libs.json.JsArray
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class ColumnServiceImpl @Inject()(columnDAO: ColumnDAO)(implicit ex: ExecutionContext)extends ColumnService {
  final var columns: Map[Int, Column] = Map.empty


  override def addColumns(data: JsArray) = {
    columnDAO.addColumns(data)
  }

  override def addColumn(column: Column) = {
    columnDAO.save(column).andThen { case Success(_) =>
      columns += column.id -> column
    }
  }

  override def saveColumn(column: Column): Future[Option[Column]] = {
    columnDAO.findAndUpdate[BSONDocument, Column](BSONDocument("id" -> column.id), column, true).andThen{ case Success(_) =>
      columns += column.id -> column
    }
  }

  override def fetchColumn(column: Int) = {
    columns.get(column)
  }

  override def fetchChildrenColumn(column: Int) = {
    columns.values.filter(_.parent == column).toSeq
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
    Future(columns.values.find(x => x.name.equals(name)))
//    columnDAO.getColumn(name)
  }

  override def getColumn(id: Int) = {
    Future(columns.values.find(x => x.id.equals(id)))
//    columnDAO.find[Column](id)
  }

  override def getColumns() = {
    Future(columns.values.toSeq.sortBy(_.order))
//    columnDAO.findAll[Column]
  }

  override def getChildrenColumn(id: Int) = {
    Future(columns.values.filter(_.parent == id).toSeq)
//    columnDAO.getChildrenColumn(id)
  }

  override def deleteColumn(id: Int) = {
    columnDAO.findAndRemove[BSONDocument, Column](BSONDocument("id" -> id)).andThen{ case Success(_) =>
      columns -= id
    }
  }

  override def deleteColumnByParent(id: Int) = {
    columnDAO.remove(BSONDocument("parent" -> id)).andThen{ case Success(true) =>
      columns.foreach {
        case (_id, c) =>
          if (c.parent == id) {
            columns -= _id
          }
      }
    }
  }

  override def getOrder(value: Int): Int = {
    columns.values.count(_.parent == value) + 1
  }
}
