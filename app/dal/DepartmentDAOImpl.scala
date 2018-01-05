package dal

import javax.inject.Inject

import models.Department
import play.api.libs.json.{JsArray, JsObject}
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.{ExecutionContext, Future}

class DepartmentDAOImpl @Inject()(implicit ec: ExecutionContext,
                                  reactiveMongoApi: ReactiveMongoApi) extends DepartmentDAO {
  override def collection = reactiveMongoApi.database.map(_.collection("department"))

  override def addDepartments(data: JsArray) = {
    val f = for(department <- data.value) yield {
      save(department.as[Department])
    }
    Future.sequence(f)
  }
}
