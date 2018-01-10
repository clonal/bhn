package dal

import reactivemongo.api.BSONSerializationPack.{Reader, Writer}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

trait BaseDAO {
  def collection: Future[BSONCollection]

  def isEmpty(implicit ec: ExecutionContext): Future[Boolean] = {
    collection.flatMap(x => x.count()).map(_ <= 0)
  }

  def update(selector: BSONDocument, modifier: BSONDocument)(implicit ec: ExecutionContext): Future[UpdateWriteResult] = {
    collection.flatMap(_.update(selector, modifier))
  }

  def update[S, T](selector: S, entity: T, upsert: Boolean = true)(implicit ec: ExecutionContext,
                                           reader: reactivemongo.bson.BSONDocumentReader[T],
                                           swriter: reactivemongo.bson.BSONDocumentWriter[S],
                                           twriter: reactivemongo.bson.BSONDocumentWriter[T]) = {
    collection.flatMap(_.findAndUpdate(selector, entity, upsert = upsert).map(_.result[T]))
  }

  def remove(id: Int)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.flatMap(_.remove(BSONDocument("id" -> id)))
  }

  def remove[S, T](selector: S)(implicit ec: ExecutionContext,
                                reader: reactivemongo.bson.BSONDocumentReader[T],
                                swriter: reactivemongo.bson.BSONDocumentWriter[S],
                                twriter: reactivemongo.bson.BSONDocumentWriter[T]): Future[Option[T]]  = {
    collection.flatMap(_.findAndRemove(selector).map(_.result[T]))
  }

  def find[T](id: Int)(implicit  reader: Reader[T], ec: ExecutionContext): Future[Option[T]] = {
    collection.flatMap(_.find(BSONDocument("id" -> id)).one[T])
  }

  def findAll[T](implicit  reader: Reader[T],ec: ExecutionContext): Future[Seq[T]] = {
    collection.flatMap(_.find(BSONDocument()).cursor[T].collect[List]())
  }

  def save[T](entity: T)(implicit writer: Writer[T],ec: ExecutionContext): Future[T] = {
    collection.flatMap(_.insert(entity)).map(_ => entity)
  }

  def getLastID(implicit ec: ExecutionContext): Future[Option[Int]] = {
    collection.flatMap(_.find(BSONDocument(), BSONDocument("id" -> 1)).sort(BSONDocument("id" -> -1)).one[BSONDocument]).map{
      case Some(o) =>
        o.getAs[Int]("id")
    }
  }
}
