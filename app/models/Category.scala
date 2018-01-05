package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Category(id: Int, name: String, desc: String, department: Int, banner: String)

object Category {
  implicit object CategoryReader extends BSONDocumentReader[Category] {
    def read(bson: BSONDocument): Category = {
      val opt: Option[Category] = for {
        id <- bson.getAs[Int]("id")
        name <- bson.getAs[String]("name")
        desc <- bson.getAs[String]("desc")
        department <- bson.getAs[Int]("department")
        banner <- bson.getAs[String]("banner")
      } yield {
        new Category(id, name, desc, department, banner)
      }
      opt.get
    }
  }

  implicit object CategoryWriter extends BSONDocumentWriter[Category] {
    def write(category: Category): BSONDocument =
      BSONDocument("id" -> category.id,
        "name" -> category.name,
        "desc" -> category.desc,
        "department" -> category.department,
        "banner" -> category.banner)
  }

  implicit val categoryFormat: OFormat[Category] = Json.format[Category]
}