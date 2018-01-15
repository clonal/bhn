package dal

import javax.inject.Inject

import models.Column
import play.api.libs.json.JsArray
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

class ColumnDAOImpl @Inject()(implicit ec: ExecutionContext,
                              reactiveMongoApi: ReactiveMongoApi) extends ColumnDAO{
  override def collection = reactiveMongoApi.database.map(_.collection("column"))

  override def addColumns(data: JsArray) = {
    val f = for(column <- data.value) yield {
      save(column.as[Column])
    }
    Future.sequence(f)
  }

  override def getColumn(name: String) = {
    collection.flatMap(_.find(BSONDocument("name" -> name)).one[Column])
  }

  override def getChildrenColumn(id: Int) = {
    collection.flatMap(_.find(BSONDocument("parent" -> id)).cursor[Column]().collect[List]())
  }
}
