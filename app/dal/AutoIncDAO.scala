package dal

import javax.inject.Inject

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONNumberLike}

import scala.concurrent.{ExecutionContext, Future}

class AutoIncDAO  @Inject() (implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) extends BaseDAO {

  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("auto_inc"))

//  implicit val autoincFormat: OFormat[AutoInc] = Json.format[AutoInc]
//  implicit val autoincWrite: OWrites[AutoInc] = Json.writes[AutoInc]

  def getNextSeq = {
    collection.flatMap(c => c.findAndModify(
      BSONDocument("seq" -> BSONDocument("$exists" -> true)),
      c.updateModifier(BSONDocument("$inc" -> BSONDocument("seq" -> 1)))
    ).map(_.result[AutoInc](AutoInc.AutoIncReader))
    )
  }
  def init = {
    collection.flatMap(c => c.insert(BSONDocument("seq" -> 1)))
  }

  def getSeq = {
    collection.flatMap(_.find(BSONDocument()).one[AutoInc](AutoInc.AutoIncReader, ec))
  }

}

case class AutoInc(seq: Long)

object AutoInc {

  implicit object AutoIncReader extends BSONDocumentReader[AutoInc] {
    def read(bson: BSONDocument): AutoInc = {
      val opt: Option[AutoInc] = for {
        seq <- bson.getAs[BSONNumberLike]("seq").map(_.toLong)
      } yield new AutoInc(seq)

      opt.get // the person is required (or let throw an exception)
    }
  }

  implicit object AutoIncWriter extends BSONDocumentWriter[AutoInc] {
    def write(autoInc: AutoInc): BSONDocument =
      BSONDocument("seq" -> autoInc.seq)
  }

//  implicit def autoIncReader: BSONDocumentReader[AutoInc] = Macros.reader[AutoInc]
//  implicit val autoIncHandler: BSONHandler[BSONDocument, AutoInc] = Macros.handler[AutoInc]
//  implicit val autoincFormat: OFormat[AutoInc] = Json.format[AutoInc]
//  implicit val autoincWrite: OWrites[AutoInc] = Json.writes[AutoInc]
}

