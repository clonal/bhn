package dal
import javax.inject.Inject

import models.Menu
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext, Future}

class MenuDAOImpl @Inject()(autoIncDAO: AutoIncDAO)(implicit ec: ExecutionContext,
                                                    reactiveMongoApi: ReactiveMongoApi) extends MenuDAO {

  implicit val jsonWrites = new Writes[Map[Int, String]] {
    def writes(o: Map[Int, String]): JsValue = {
      val keyAsString = o.map { kv => kv._1 -> kv._2} // Convert to Map[String,Int] which it can convert
      Json.toJson(keyAsString)
    }
  }

  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("menu"))

//  override def save(menu: Menu) = {
//    collection.flatMap(_.insert(menu)).map(_ => menu)
//  }

  override def addMenu(menu: JsObject) = {
    (menu.validate[Menu] map { case s => save(s)}).get
/*    collection.flatMap(_.insert(menu)).map{ _ =>
      new Menu(
        (menu \ "id").as[Int],
        (menu \ "name").as[String],
        (menu \ "parent").as[Int],
        (menu \ "banner").as[Map[String, String]],
        (menu \ "desc").as[String],
        (menu \ "content").as[String],
        (menu \ "order").as[Int],
        (menu \ "required").as[Boolean]
      )}*/
  }

  override def addMenus(data: JsArray) = {
    val f = for(menu <- data.value) yield {
      addMenu(menu.as[JsObject])
    }
    Future.sequence(f)
  }

//  override def find(id: Int) = {
//    collection.flatMap(_.find(BSONDocument("id" -> id)).one[Menu])
//  }

  override def find(name: String) = {
    collection.flatMap(_.find(BSONDocument("name" -> name)).one[Menu])
  }

  override def findByOrder(parent: Int, order: Int) = {
    collection.flatMap(_.find(BSONDocument("parent" -> parent,"order" -> order)).one[Menu])
  }

//  override def findAll() = {
//    collection.flatMap(_.find(BSONDocument()).cursor[Menu].collect[List]())
//  }

  override def findChildrenOfMenu(menu: Int) = {
    collection.flatMap(_.find(BSONDocument("parent" -> menu)).
      sort(BSONDocument("order" -> 1)).cursor[Menu].collect[List]())
  }

  override def update(menu: Menu) = {
    val b1 = BSONDocument("2" -> 2)
    val b2 = BSONDocument(menu.banner)
    collection.flatMap(_.update(BSONDocument("id" -> menu.id), BSONDocument("$set" ->
      BSONDocument("name" -> menu.name, "parent" -> menu.parent, "banner" -> menu.banner,
        "desc" -> menu.desc, "content" -> menu.content)), upsert = false))
  }

//  override def update(selector: BSONDocument, modifier: BSONDocument) = {
//    collection.flatMap(_.update(selector, modifier))
//  }

//  override def remove(id: Int) = {
//    collection.flatMap(_.remove(BSONDocument("id" -> id)))
//  }

  override def remove(name: String) = {
    collection.flatMap(_.remove(BSONDocument("name" -> name)))
  }

/*  override def getLastID() = {
    collection.flatMap(_.find(BSONDocument(),
      BSONDocument("id" -> 1)).sort(BSONDocument("id" -> -1)).one[BSONDocument]).map{
      case Some(o) => o.getAs[Int]("id")
    }
  }*/
}
