package controllers.product

import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

import akka.actor.ActorSystem
import models.{Category, Product, Sku}
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
  def addProduct() = Action.async(parse.json) {
    implicit request =>
      val jsObject = request.body
      val skus = (jsObject \ "sku").as[JsArray]
      val id = productService.CATEGORY_AUTO_ID.addAndGet(1)
      val sku = for(jValue <- skus.value) yield {
        val attributes = (jValue \ "attributes").as[JsArray].value map { v =>
          val key = (v \ "key").as[String]
          val value = (v \ "value").as[String]
          Map("key" -> key,"value" -> value)
        }
        Sku(productService.SKU_AUTO_ID.addAndGet(1),
          id,
          (jValue \ "name").as[String],
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

      val product = Product(id,
        (jsObject \ "name").as[String],
        (jsObject \ "desc").as[String],
        (jsObject \ "category").as[Array[Int]],
        sku.toArray.map(_.id),
        (jsObject \ "amazonLink").as[String]
      )
      productService.addSkus(sku).flatMap{ _ =>
        productService.addProduct(product).map(m => Ok(Json.toJsObject(m)))
      }
  }
  //修改产品
  def editProduct(product: Int) = Action.async(parse.json) {
    implicit request =>
      val p = request.body.as[Product]
      productService.updateProduct(BSONDocument("id" -> p.id), p).map {
        case Some(_) => Ok(Json.obj("info" -> "success"))
        case None => BadRequest(Json.obj("error" -> "none to update"))
      }
      /*productService.findProduct(product).flatMap{
        case Some(p) =>
          var modifier = BSONDocument.empty
          (request.body \ "name").asOpt[String].foreach{ v =>
            modifier ++= ("name" -> v)
          }
          (request.body \ "desc").asOpt[String].foreach{ v =>
            modifier ++= ("desc" -> v)
          }
          (request.body \ "category").asOpt[Array[Int]].foreach{ v =>
            modifier ++= ("category" -> v)
          }
          (request.body \ "amazonLink").asOpt[String].foreach{ v =>
            modifier ++= ("amazonLink" -> v)
          }
          productService.updateProduct(BSONDocument("id" -> product), modifier).map(_ => Ok(Json.obj("info" -> "ok")))
        case None =>
          Future(BadRequest(Json.obj("error" -> "wrong product")))
      }*/
  }

  //删除产品
  def removeProduct(product: Int) = Action.async {
    implicit request =>
      productService.removeProduct(product).map {
        case Some(p) =>
          p.sku.foreach{ s => //TODO 是否要考虑等这个业务完成后再返回结果
            productService.removeSku(s)
          }
          Ok(Json.obj("info" -> "delete complete"))
        case None =>
          BadRequest(Json.obj("error" -> "none to delete"))
      }
  }

  //产品列表
  def listProducts() = Action.async {
    implicit request =>
      productService.queryProducts().map{ list =>
        Ok(JsArray(list.map(x => Json.toJson(x))))
//        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.name)))
      }
  }

  //查找产品
  def findProduct(product: Int) = Action.async {
    implicit request =>
      productService.findProduct(product).map{
        case Some(c) => Ok(Json.obj("product" -> Json.toJsObject(c)))
        case None => BadRequest(Json.obj("error" -> "wrong product"))
      }
  }

  //添加sku
  def addSku(product: Int) = Action.async(parse.json) {
    implicit request =>
          val jValue = request.body.as[JsObject]
          val attributes = (jValue \ "attributes").as[JsArray].value map { v =>
            val key = (v \ "key").as[String]
            val value = (v \ "value").as[String]
            Map("key" -> key,"value" -> value)
          }
          val id = productService.SKU_AUTO_ID.addAndGet(1)
          val sku = Sku(id,
            product,
            (jValue \ "name").as[String],
            attributes.toArray,
            (jValue \ "content").as[String],
            (jValue \ "price").as[Double],
            (jValue \ "sellPrice").as[Double],
            (jValue \ "asin").as[String],
            (jValue \ "stock").as[Int],
            (jValue \ "show").as[Int],
            (jValue \ "images").as[Map[String, String]]
          )

          productService.addSku(sku).flatMap{ s =>
            productService.getProduct(product) match {
              case Some(p) =>
                productService.updateProduct(BSONDocument("id" -> product),
                  p.copy(sku = p.sku :+ s.id)).map(_ => Ok(Json.toJsObject(sku)))
              case _ =>
                Future(BadRequest(Json.obj("error" -> "no product to add")))
            }
          }

      /*productService.findProduct(product).flatMap {
        case Some(p) =>
          val jValue = request.body.as[JsObject]
          val attributes = (jValue \ "attributes").as[JsArray].value map { v =>
            val key = (v \ "key").as[String]
            val value = (v \ "value").as[String]
            Map("key" -> key,"value" -> value)
          }
          val id = productService.SKU_AUTO_ID.addAndGet(1)
          val sku = Sku(id,
              product,
              (jValue \ "name").as[String],
              attributes.toArray,
              (jValue \ "content").as[String],
              (jValue \ "price").as[Double],
              (jValue \ "sellPrice").as[Double],
              (jValue \ "asin").as[String],
              (jValue \ "stock").as[Int],
              (jValue \ "show").as[Int],
              (jValue \ "images").as[Map[String, String]]
            )

          productService.addSku(sku).flatMap{ _ =>
            val modifier = BSONDocument("sku" -> BSONArray(p.sku :+ id))
            productService.updateProduct(BSONDocument("id" -> product),
              modifier).map(m => Ok(Json.toJsObject(sku)))
          }
        case None =>
          Future(BadRequest(Json.obj("error" -> "no product to add")))
      }*/
  }
  //删除sku
  def removeSku(sku: Int) = Action.async {
    implicit request =>
      productService.removeSku(sku).map { case Some(s) =>
        productService.getProduct(s.product).map{ p =>
          val _p = p.copy(sku = p.sku.filterNot(_ == s.id))
          productService.updateProduct(BSONDocument("id" -> p.id), _p)
        }
        Ok(Json.obj("info" -> "delete completed!"))
      }
      /*productService.findSku(sku).flatMap {
        case Some(s) =>
          productService.removeSku(sku).flatMap{ _ =>
            productService.findProduct(s.product).flatMap{
              case Some(p) =>
                val modifier = BSONDocument("sku" -> BSONArray(p.sku.filterNot(_ == s.id)))
                productService.updateProduct(BSONDocument("id" -> p.id),
                  modifier).map(m => Ok(Json.obj("info" -> "delete completed!")))
              case None =>
                Future(Ok(Json.obj("info" -> "delete completed!")))
            }
          }
        case None =>
          Future(BadRequest(Json.obj("error" -> "nothing to delete")))
      }*/
  }

  //编辑sku
  def editSku() = Action.async(parse.json) {
    implicit  request =>
      val s = request.body.as[Sku]
      productService.updateSku(BSONDocument("id" -> s.id), s).flatMap { case Some(sk) =>
        productService.updateProductSku(s.id, sk.id).map(_ =>
          Ok(Json.obj("info" -> "ok"))
        )
      }
      /*productService.findSku(sku).flatMap{
        case Some(s) =>
          var modifier = BSONDocument.empty
          (request.body \ "name").asOpt[String].foreach{ v =>
            modifier ++= ("name" -> v)
          }
          (request.body \ "attributes").asOpt[Array[Map[String, String]]].foreach{ v =>
            modifier ++= ("attributes" -> v)
          }
          (request.body \ "content").asOpt[String].foreach{ v =>
            modifier ++= ("content" -> v)
          }
          (request.body \ "price").asOpt[Double].foreach{ v =>
            modifier ++= ("price" -> v)
          }
          (request.body \ "sellPrice").asOpt[Double].foreach{ v =>
            modifier ++= ("sellPrice" -> v)
          }
          (request.body \ "asin").asOpt[String].foreach{ v =>
            modifier ++= ("asin" -> v)
          }
          (request.body \ "stock").asOpt[Int].foreach{ v =>
            modifier ++= ("stock" -> v)
          }
          (request.body \ "show").asOpt[Int].foreach{ v =>
            modifier ++= ("show" -> v)
          }
          (request.body \ "images").asOpt[Map[String, String]].foreach{ v =>
            modifier ++= ("images" -> v)
          }
          productService.updateSku(BSONDocument("id" -> sku), modifier).map(_ => Ok(Json.obj("info" -> "ok")))
        case None =>
          Future(BadRequest(Json.obj("error" -> "wrong sku")))
      }*/
  }

  //查找sku
  def findSku(sku: Int) = Action.async {
    implicit request =>
      productService.findSku(sku).map{
        case Some(s) => Ok(Json.toJsObject(s))
        case None => BadRequest(Json.obj("error" -> "wrong sku"))
      }
  }

  def findSkusByProduct(product: Int) = Action.async {
    implicit request =>
      productService.findSkusByProduct(product).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.name)))
      }
  }
  //sku列表
  def listSkus() = Action.async {
    implicit request =>
      productService.querySkus().map{ list =>
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
        productService.updateProductCategory(c.id, cate.id).map(_ =>
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
  def findComment(product: Option[Int], sku: Option[Int]) = Action.async {
    implicit request =>
      productService.findComment(product, sku).map{ list =>
        Ok(list.foldLeft(JsObject.empty)((acc, x) => acc ++ Json.obj(x.id.toString -> x.title)))
      }
  }
}
