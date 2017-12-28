package dal

import javax.inject.Inject

import models.Comment
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext

class CommentDAOImpl @Inject()(implicit ec: ExecutionContext,
                                reactiveMongoApi: ReactiveMongoApi) extends CommentDAO{
  override def collection = reactiveMongoApi.database.map(_.collection("comment"))

  override def findComment(item: Option[Int], sku: Option[Int]) = {
    val selector1 = item.map(x => BSONDocument("product" -> x)).getOrElse(BSONDocument())
    val selector2 = item.map(x => BSONDocument("product" -> x)).getOrElse(BSONDocument())
    collection.flatMap(_.find(selector1 ++ selector2).cursor[Comment].collect[List]())
  }
}
