package boot

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import dal.AutoIncDAO
import play.api.libs.json.{JsArray, Json}
import services._

import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.{Failure, Success}

@Singleton
class Initial @Inject()(autoIncDao: AutoIncDAO,
                        menuService: MenuService,
                        articleService: ArticleService,
                        authService: AuthTokenService,
                        mwsService: MWSService,
                        productService: ProductService,
                        questionService: QuestionService,
                        feedbackService: FeedbackService,
                        columnService: ColumnService,
                       )(implicit ec: ExecutionContext){

  //初始化autoInc
  autoIncDao.getSeq.onComplete{
    case Success(v) =>
      v match {
        case Some(_) =>
        case _ => autoIncDao.init
      }
    case Failure(e) => e.printStackTrace()
  }

  //初始化数据库
  val file = Source.fromFile(Paths.get("./conf/InitData.json").toUri)
  Json.parse(file.mkString) match {
    case ary: JsArray =>
      for(json <- ary.value) {
        val data = (json \ "data").as[JsArray]
        (json \ "collection").as[String] match {
          case "menu" => menuService.init(data)
          case "article" => articleService.init(data)
          case "department" => productService.initDepartment(data)
          case "category" => productService.initCategory(data)
          case "product" => productService.initProduct(data)
          case "receipt_template" => mwsService.initReceiptTemplate(data)
          case "question" => questionService.initQuestion(data)
          case "feedback" => feedbackService.initFeedback(data)
          case "column" => columnService.initColumn(data)
          case _ =>
        }
      }
    case _ =>
  }
}
