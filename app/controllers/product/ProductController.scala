package controllers.product

import java.io.{File, IOException}
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import javax.inject.Inject

import akka.actor.ActorSystem
import models.{Category, Department, Product, TreeNode}
import org.joda.time.DateTime
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import reactivemongo.bson.BSONDocument
import services.ProductService

import scala.concurrent.{ExecutionContext, Future}

class ProductController @Inject()(
                                  config: Configuration,
                                  components: ControllerComponents,
                                  productService: ProductService,
                                  system: ActorSystem
                                ) (implicit ec: ExecutionContext)extends AbstractController(components) with I18nSupport  {

//  final val IMG_PATH = System.getProperty("user.dir") + config.underlying.getString("play.assets.path") + File.separator + "images"
  final val IMAGE_PREFIX = "img"
  final val IMG_DIR_PREFIX = "product_"
  final val TEMP_IMG_DIR_PREFIX = "temp_"
//  final val ASSETS_BASIC = config.underlying.getString("play.assets.path").tail
  final val ASSETS_BASIC = Paths.get(System.getProperty("user.dir")).getRoot.resolve(config.underlying.getString("play.assets.imgPath")).toString
  final val IMG_PATH = Paths.get( ASSETS_BASIC + File.separator + IMAGE_PREFIX)
  final val TEMP_IMG_PATH_NEW = Paths.get(IMAGE_PREFIX + File.separator +"temp_product")

//  Paths.get(System.getProperty("user.dir")).getRoot.resolve("demo\\shop-demo")

  def productImgDirectory(id: Int) = {
    Paths.get(IMAGE_PREFIX + File.separator + IMG_DIR_PREFIX + id)
  }

  def productTempImgDirectory(id: Option[Int]) = id match {
    case Some(pid) => Paths.get(productImgDirectory(pid).toString + File.separator + TEMP_IMG_DIR_PREFIX.init)
    case None => TEMP_IMG_PATH_NEW
  }

  def formatImagePrefix(id: Int, index: String) = {
    IMG_DIR_PREFIX + id + "_" + index
  }

  def formatImageName(name: String, id: Int, index: String) = {
    val suffix = name.substring(name.lastIndexOf("."))
    formatImagePrefix(id, index) + suffix
  }

  def mkDir(directory: Path): Unit = {
    val _directory = Paths.get(ASSETS_BASIC + File.separator + directory.toString)
    val directoryFile = _directory.toFile
    if (!directoryFile.exists() || !directoryFile.isDirectory) {
      Files.createDirectory(_directory)
    }
  }

  def moveTempPic(image: String, id: Int, index: String, dir: Path) = {
    if (!image.equals("")) {
      val formatName = formatImageName(image, id, index)
      val path = dir.toString + File.separator + formatName
      //      Files.move(Paths.get(IMG_PATH + File.separator + image), Paths.get(path))
      removePicInDir(dir, formatImagePrefix(id, index))
      Files.move(Paths.get(ASSETS_BASIC + File.separator + image),
        Paths.get(ASSETS_BASIC + File.separator + path),
        StandardCopyOption.REPLACE_EXISTING)
      path
    } else {
      image
    }
  }

  def removePicInDir(dir: Path, prefix: String) = {
    val it = Files.newDirectoryStream(Paths.get(ASSETS_BASIC + File.separator + dir.toString)).iterator()
    while(it.hasNext) {
      val next = it.next()
      if (next.getFileName.toString.startsWith(prefix)) {
        Files.deleteIfExists(next)
      }
    }
  }

  def tempPicName(index: Int, suffix: String) = {
    val now = DateTime.now().toString("yyyyMMddHHmmss")
    TEMP_IMG_DIR_PREFIX + index + "_" + now + suffix
  }

  //添加产品
  def addProduct() = Action.async(parse.json) {
    implicit request =>
      val product = request.body.as[Product]
      val id = if (product.id == 0) {
        val _id = productService.PRODUCT_AUTO_ID.incrementAndGet()
//        Files.createDirectories(productImgDirectory(_id))
        _id
      } else {
        product.id
      }
       val dir = productImgDirectory(id)
       mkDir(dir)
         //更改temp为正式的图片名,正式图片命名规则为product_id_index.后缀
        try {
          val images = product.images map { case(index, image) =>
            if (image == "") {
              removePicInDir(dir, formatImagePrefix(id, index))
              (index, image)
            } else {
              val formatName = if (product.id != 0) {
                val imagePath = Paths.get(ASSETS_BASIC + File.separator + image).getParent
                val lastName = if (imagePath == null) "" else imagePath.getFileName.toString
                if (lastName == "temp_product" || lastName == "temp") {
                  moveTempPic(image, id, index, dir)
                } else {
                  image
                }
              } else {
                moveTempPic(image, id, index, dir)
              }
              (index, formatName)
            }

          }
          productService.updateOrSaveProduct(product.copy(id = id, images = images)).flatMap{ s =>
            Future(Ok(Json.obj("ok" -> "ok")))
          }
        } catch {
          case e: IOException =>
            e.printStackTrace()
            Future.successful(BadRequest(Json.obj("error" -> "upload error")))
        }

  }
  //删除产品
  def removeProduct(product: Int) = Action.async {
    implicit request =>
      productService.removeProduct(product).map { case Some(_) =>
        Ok(Json.obj("info" -> "delete completed!"))
      }
  }

  //编辑产品
  def editProduct() = Action.async(parse.json) {
    implicit  request =>
      val s = request.body.as[Product]
      productService.updateProduct(BSONDocument("id" -> s.id), s).flatMap { case Some(sk) =>
        Future(Ok(Json.obj("info" -> "ok")))
      }
  }

  //查找产品
  def findProduct(product: Option[Int]) = Action.async {
    implicit request =>
      product match {
        case Some(id) =>
          productService.findProduct(id).map{
            case Some(s) => Ok(Json.obj("product" -> Json.toJsObject(s)))
            case None => BadRequest(Json.obj("error" -> "wrong product"))
          }
        case _ =>
          Future(Ok(Json.obj("error" -> "empty")))
      }

  }

  def findProductsByParent(parent: Int) = Action.async {
    implicit request =>
      productService.findProductsByParent(parent).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.name)))
      }
  }
  //sku列表
  def listProducts(num: Option[Int]) = Action.async {
    implicit request =>
      productService.queryProducts(num).map{ list =>
        Ok(JsArray(list.map(x => Json.toJson(x))))
      }
  }

  def queryTopProducts() = Action {
    val list = productService.queryTopProducts().map { p =>
      p.addChildren(productService.getProductsByParent(p.id))
    }
    Ok(Json.toJson(list))
  }

  // 添加产品类型
  def addCategory() = Action.async(parse.json) {
    implicit request =>
      val id = productService.CATEGORY_AUTO_ID.incrementAndGet()
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      productService.addCategory(jsObject.as[Category]).map(m => Ok(Json.toJsObject(m)))
  }

  def addDepartment() = Action.async(parse.json) {
    implicit request =>
      val id = productService.DEPARTMENT_AUTO_ID.incrementAndGet()
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      productService.addDepartment(jsObject.as[Department]).map(m => Ok(Json.toJsObject(m)))
  }

  def removeDepartment(department: Int) = Action.async {
    implicit request =>
      productService.removeDepartment(department).flatMap {
        case Some(c) =>
          productService.removeCategoryByDepartment(c.id).map{ case true =>
            Ok(Json.obj("info" -> s"delete ${c.name} complete"))
          }
      }
  }

  def updateProductPic(index: Option[Int], pid: Option[Int]) = Action.async(parse.multipartFormData) {
    implicit request =>
      try {
        (for{
          _index <- index
          file <- request.body.files.headOption
        } yield {
          val filename = tempPicName(_index, file.filename.substring(file.filename.lastIndexOf(".")))
          val directory = productTempImgDirectory(pid)
          mkDir(directory)
          removePicInDir(directory, TEMP_IMG_DIR_PREFIX + _index + "_")
          val path = directory.toString + File.separator + filename
          file.ref.moveTo(Paths.get(ASSETS_BASIC + File.separator + path), replace = true)
          Future.successful(Ok(Json.obj("index" -> _index, "img" -> path)))
        }).getOrElse(Future(BadRequest("no file to upload")))
      } catch  {
        case e: Exception =>
          e.printStackTrace()
          Future.successful(BadRequest(Json.obj("error" -> "upload error")))
      }
  }

  //删除产品类型
  def removeCategory(category: Int) = Action.async {
    implicit request =>
      productService.removeCategory(category).map {
        case Some(c) => Ok(Json.obj("info" -> s"delete ${c.name} complete"))
      }
  }

  //修改产品类型
  def editCategory() = Action.async(parse.json) {
    implicit request =>
      val c = request.body.as[Category]
      productService.updateCategory(BSONDocument("id" -> c.id), c).flatMap { case Some(cate) =>
        productService.updateProductCategory(c.id, cate.id).map(_ =>
          Ok(Json.obj("info" -> s"edit ${c.name} ok"))
        )
      }
  }

  def saveCategory() = Action.async(parse.json) {
    implicit request =>
      var json = request.body.as[JsObject]
      if (json("id").as[Int] == 0) {
        json ++= Json.obj("id" -> productService.CATEGORY_AUTO_ID.incrementAndGet())
//        json ++= Json.obj("order" -> columnService.getOrder(json("parent").as[Int]))
      }
      val category = json.as[Category]
      productService.saveCategory(category).map { case Some(c) =>
        Ok(Json.obj("info" -> "add category success!"))
      }
  }

  def saveDepartment() = Action.async(parse.json) {
    implicit request =>
      var json = request.body.as[JsObject]
      if (json("id").as[Int] == 0) {
        json ++= Json.obj("id" -> productService.DEPARTMENT_AUTO_ID.incrementAndGet())
        json ++= Json.obj("order" -> productService.getDepartmentOrder())
      }
      val department = json.as[Department]
      productService.saveDepartment(department).map { case _ =>
        Ok(Json.obj("info" -> "add department success!"))
      }
  }

  def addCategoryBanner(category: Option[Int]) = Action.async(parse.multipartFormData) {
    implicit request =>
      try {
        (for{
              cate <- category
              file <- request.body.files.headOption
            } yield {
            val filename = System.currentTimeMillis() + "_" + file.filename
            file.ref.moveTo(Paths.get(IMG_PATH.toString + File.separator + filename), replace = true)
            productService.updateCategoryBanner(cate, filename)
            Future.successful(Ok(Json.obj("msg" -> "upload completed!")))
        }).getOrElse(Future(BadRequest("no file to upload")))
      } catch  {
        case e: Exception =>
          e.printStackTrace()
          Future.successful(BadRequest(Json.obj("error" -> "upload error")))
      }
  }


  //产品类型列表
  def listCategories(num: Option[Int]) = Action.async {
    implicit request =>
      productService.queryCategories(num).map{ list =>
        Ok(JsArray(list.map(x => Json.toJsObject(x))))
      }
  }

  def listDepartments() = Action.async {
    implicit request =>
      productService.queryDepartments().flatMap{ list =>
        makeDepartmentNodes(list).map(x => Ok(Json.toJson(x)))
      }
  }

  def makeDepartmentNodes(list: Seq[Department]) = {
    val futures = list.map{ department =>
      productService.findCategoriesByDepartment(department.id).map { categories =>
        val children = categories.map(x => TreeNode(x.id, x.name, 0, x.desc, Seq.empty))
        TreeNode(department.id, department.name, department.order, department.desc, children)
      }
    }
    Future.sequence(futures)
  }

  def listTopCategories() = Action.async {
    implicit request =>
      productService.queryTopCategories().map { list =>
        Ok(JsArray(list.map(x => Json.toJsObject(x))))
      }
  }

  //查找产品类型
  def findCategory(category: Option[Int]) = Action.async {
    implicit request =>
      category match {
        case Some(id) =>
          productService.findCategory(id).map{
            case Some(c) => Ok(Json.obj("category" -> Json.toJsObject(c)))
            case None => BadRequest(Json.obj("error" -> "wrong menu"))
          }
        case None =>
          Future(Ok(Json.obj("error" -> "empty")))
      }
  }

  //查找产品类型
  def findDepartment(department: Option[Int]) = Action.async {
    implicit request =>
      department match {
        case Some(id) =>
          productService.findDepartment(id).map{
            case Some(c) => Ok(Json.obj("department" -> Json.toJsObject(c)))
            case None => BadRequest(Json.obj("error" -> "wrong department"))
          }
        case None =>
          Future(Ok(Json.obj("error" -> "empty")))
      }
  }
  //评论列表
  def listComments() = Action.async {
    implicit request =>
      productService.queryComments().map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.title)))
      }
  }
  //查找评论
  def commentDetail(comment: Int) = Action.async {
    implicit request =>
      productService.findComment(comment).map{
        case Some(c) => Ok(Json.toJsObject(c))
        case None => BadRequest(Json.obj("error" -> "wrong menu"))
      }
  }

  //查找评论 根据产品和型号
  def findComment(product: Option[Int]) = Action.async {
    implicit request =>
      productService.findComment(product).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.title)))
      }
  }
}
