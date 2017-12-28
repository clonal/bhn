package dal

import models.Comment

import scala.concurrent.Future

trait CommentDAO extends BaseDAO{
  def findComment(item: Option[Int], sku: Option[Int]): Future[Seq[Comment]]
}
