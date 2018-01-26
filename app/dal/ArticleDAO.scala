package dal

import models.Article
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.api.commands.UpdateWriteResult

import scala.concurrent.Future

trait ArticleDAO extends BaseDAO{

  def addArticles(data: JsArray): Future[Seq[Article]]

  def queryArticles(): Future[Seq[Article]]

  def queryArticles(column: Int, acs: Int): Future[Seq[Article]]

  def findByOrder(column: Int, order: Int): Future[Option[Article]]

  def update(article: Article): Future[UpdateWriteResult]

  def addArticle(data: JsObject): Future[Article]
//  def getLastID(): Future[Option[Int]]
}
