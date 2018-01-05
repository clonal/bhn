package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Department(id: Int, name: String, desc: String) {}

object Department {
  implicit object DepartmentReader extends BSONDocumentReader[Department] {
    def read(bson: BSONDocument): Department = {
      val opt: Option[Department] = for {
        id <- bson.getAs[Int]("id")
        name <- bson.getAs[String]("name")
        desc <- bson.getAs[String]("desc")
      } yield {
        new Department(id, name, desc)
      }
      opt.get
    }
  }

  implicit object DepartmentWriter extends BSONDocumentWriter[Department] {
    def write(department: Department): BSONDocument =
      BSONDocument("id" -> department.id,
        "name" -> department.name,
        "desc" -> department.desc)
  }

  implicit val departmentFormat: OFormat[Department] = Json.format[Department]
}
