package dal

import models.Comment

import scala.concurrent.Future

trait CommentDAO extends BaseDAO{
  def findComment(product: Option[Int], sku: Option[Int]): Future[Seq[Comment]]
}
