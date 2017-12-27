package dal

import javax.inject.Inject

import models.Receipt
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

class ReceiptDAOImpl @Inject()(implicit ec: ExecutionContext,
                               reactiveMongoApi: ReactiveMongoApi) extends ReceiptDAO {
  override def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("receipt"))

  override def findReceipt(receipt: Receipt): Future[Option[Receipt]] = {
    collection.flatMap(_.find(BSONDocument("amazonOrderId" -> receipt.amazonOrderId, "buyerName" -> receipt.buyerName)).one[Receipt])
  }
}
