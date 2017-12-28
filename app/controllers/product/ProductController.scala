package controllers.product

import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

import akka.actor.ActorSystem
import models.{Category, Item, Product}
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

  final val IMG_PATH = System.getProperty("user.dir") + config.underlying.getString("play.assets.path") + File.separator + "images"

  //添加产品
  def addItem() = Action.async(parse.json) {
    implicit request =>
      val jsObject = request.body
      val productData = (jsObject \ "product").as[JsArray]
      val id = productService.CATEGORY_AUTO_ID.addAndGet(1)
      val products = for(jValue <- productData.value) yield {
        val attributes = (jValue \ "attributes").as[JsArray].value map { v =>
          val key = (v \ "key").as[String]
          val value = (v \ "value").as[String]
          Map("key" -> key,"value" -> value)
        }
        Product(productService.PRODUCT_AUTO_ID.addAndGet(1),
          id,
          (jValue \ "name").as[String],
          (jValue \ "sku").as[String],
          attributes.toArray,
          (jValue \ "content").as[String],
          (jValue \ "price").as[Double],
          (jValue \ "sellPrice").as[Double],
          (jValue \ "asin").as[String],
          (jValue \ "stock").as[Int],
          (jValue \ "show").as[Int],
          (jValue \ "images").as[Map[String, String]]
        )
      }

      val item = Item(id,
        (jsObject \ "name").as[String],
        (jsObject \ "desc").as[String],
        (jsObject \ "category").as[Array[Int]],
        products.toArray.map(_.id),
        (jsObject \ "amazonLink").as[String]
      )
      productService.addProducts(products).flatMap{ _ =>
        productService.addItem(item).map(m => Ok(Json.toJsObject(m)))
      }
  }
  //修改产品
  def editItem(item: Int) = Action.async(parse.json) {
    implicit request =>
      val p = request.body.as[Item]
      productService.updateItem(BSONDocument("id" -> p.id), p).map {
        case Some(_) => Ok(Json.obj("info" -> "success"))
        case None => BadRequest(Json.obj("error" -> "none to update"))
      }
  }

  //删除产品
  def removeItem(item: Int) = Action.async {
    implicit request =>
      productService.removeItem(item).map {
        case Some(p) =>
          p.product.foreach{ s => //TODO 是否要考虑等这个业务完成后再返回结果
            productService.removeProduct(s)
          }
          Ok(Json.obj("info" -> "delete complete"))
        case None =>
          BadRequest(Json.obj("error" -> "none to delete"))
      }
  }

  //产品列表
  def listItems() = Action.async {
    implicit request =>
      productService.queryItems().map{ list =>
        Ok(JsArray(list.map(x => Json.toJson(x))))
      }
  }

  //查找产品
  def findItem(item: Int) = Action.async {
    implicit request =>
      productService.findItem(item).map{
        case Some(c) => Ok(Json.obj("product" -> Json.toJsObject(c)))
        case None => BadRequest(Json.obj("error" -> "wrong product"))
      }
  }

  //添加sku
  def addProduct(item: Int) = Action.async(parse.json) {
    implicit request =>
          val jValue = request.body.as[JsObject]
          val attributes = (jValue \ "attributes").as[JsArray].value map { v =>
            val key = (v \ "key").as[String]
            val value = (v \ "value").as[String]
            Map("key" -> key,"value" -> value)
          }
          val id = productService.PRODUCT_AUTO_ID.addAndGet(1)
          val product = Product(id,
            item,
            (jValue \ "name").as[String],
            (jValue \ "sku").as[String],
            attributes.toArray,
            (jValue \ "content").as[String],
            (jValue \ "price").as[Double],
            (jValue \ "sellPrice").as[Double],
            (jValue \ "asin").as[String],
            (jValue \ "stock").as[Int],
            (jValue \ "show").as[Int],
            (jValue \ "images").as[Map[String, String]]
          )

          productService.addProduct(product).flatMap{ s =>
            productService.getItem(item) match {
              case Some(p) =>
                productService.updateItem(BSONDocument("id" -> item),
                  p.copy(product = p.product :+ s.id)).map(_ => Ok(Json.toJsObject(product)))
              case _ =>
                Future(BadRequest(Json.obj("error" -> "no product to add")))
            }
          }
  }
  //删除sku
  def removeProduct(product: Int) = Action.async {
    implicit request =>
      productService.removeProduct(product).map { case Some(s) =>
        productService.getItem(s.item).map{ p =>
          val _p = p.copy(product = p.product.filterNot(_ == s.id))
          productService.updateItem(BSONDocument("id" -> p.id), _p)
        }
        Ok(Json.obj("info" -> "delete completed!"))
      }
  }

  //编辑sku
  def editProduct() = Action.async(parse.json) {
    implicit  request =>
      val s = request.body.as[Product]
      productService.updateProduct(BSONDocument("id" -> s.id), s).flatMap { case Some(sk) =>
        productService.updateItemProduct(s.id, sk.id).map(_ =>
          Ok(Json.obj("info" -> "ok"))
        )
      }
  }

  //查找sku
  def findProduct(product: Int) = Action.async {
    implicit request =>
      productService.findProduct(product).map{
        case Some(s) => Ok(Json.toJsObject(s))
        case None => BadRequest(Json.obj("error" -> "wrong product"))
      }
  }

  def findProductsByItem(item: Int) = Action.async {
    implicit request =>
      productService.findProductsByItem(item).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.name)))
      }
  }
  //sku列表
  def listProducts() = Action.async {
    implicit request =>
      productService.queryProducts().map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.name)))
      }
  }
  // 添加产品类型
  def addCategory() = Action.async(parse.json) {
    implicit request =>
      val id = productService.CATEGORY_AUTO_ID.addAndGet(1)
      val jsObject = request.body.as[JsObject] ++ Json.obj("id" -> id)
      productService.addCategory(jsObject.as[Category]).map(m => Ok(Json.toJsObject(m)))
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
        productService.updateItemCategory(c.id, cate.id).map(_ =>
          Ok(Json.obj("info" -> s"edit ${c.name} ok"))
        )
      }
     /* productService.findCategory(category).flatMap{
        case Some(c) =>
          var modifier = BSONDocument.empty
          (request.body \ "parent").asOpt[Int].foreach{ v =>
            modifier ++= ("parent" -> v)
          }
          (request.body \ "name").asOpt[String].foreach{ v =>
            modifier ++= ("name" -> v)
          }
          (request.body \ "desc").asOpt[String].foreach{ v =>
            modifier ++= ("desc" -> v)
          }
          productService.updateCategory(BSONDocument("id" -> category), modifier).map(_ => Ok(Json.obj("info" -> "ok")))
        case None =>
          Future(BadRequest(Json.obj("error" -> "wrong category")))
      }*/
  }

  def addCategoryBanner(category: Option[Int]) = Action.async(parse.multipartFormData) {
    implicit request =>
      try {
        (for{
              cate <- category
              file <- request.body.files.headOption
            } yield {
            val filename = System.currentTimeMillis() + "_" + file.filename
            file.ref.moveTo(Paths.get(IMG_PATH + File.separator + filename), replace = true)
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
  def listCategories() = Action.async {
    implicit request =>
      productService.queryCategories().map{ list =>
        Ok(JsArray(list.map(x => Json.toJsObject(x))))
      }
  }

  def listTopCategories() = Action.async {
    implicit request =>
      productService.queryTopCategories().map { list =>
        Ok(JsArray(list.map(x => Json.toJsObject(x))))
      }
  }

  //查找产品类型
  def findCategory(category: Int) = Action.async {
    implicit request =>
      productService.findCategory(category).map{
        case Some(c) => Ok(Json.obj("category" -> Json.toJsObject(c)))
        case None => BadRequest(Json.obj("error" -> "wrong menu"))
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
  def findComment(item: Option[Int], product: Option[Int]) = Action.async {
    implicit request =>
      productService.findComment(item, product).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.title)))
      }
  }
}
