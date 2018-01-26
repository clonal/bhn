package services

import java.util.concurrent.atomic.AtomicInteger

import models.Article
import play.api.libs.json.{JsArray, JsObject}
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait ArticleService {

  val ARTICLE_AUTO_ID = new AtomicInteger()
  //初始化
  def init(data: JsArray)

  def addArticles(data: JsArray): Future[Seq[Article]]

  def getLastID(): Future[Option[Int]]

  def queryAllArticles(): Future[Seq[Article]]

  def queryArticles(column: Int, asc: Int): Future[Seq[Article]]

  def findArticle(article: Int): Future[Option[Article]]

  def findArticleByOrder(column: Int, order: Int): Future[Option[Article]]

  def updateArticle(article: Article): Future[Article]

  def updateArticle(selector: BSONDocument, modifier: BSONDocument): Future[UpdateWriteResult]

  def removeArticle(article: Int):  Future[Boolean]

  def addArticle(data: JsObject): Future[Article]

  def getOrder(col: Int): Int

  def saveArticle(article: Article): Future[Option[Article]]
}
