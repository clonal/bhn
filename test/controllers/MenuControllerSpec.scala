package controllers

import com.google.gson.JsonObject
import controllers.system.MenuController
import org.specs2.matcher.JsonMatchers
import org.specs2.mock._
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.test.{FakeHeaders, FakeRequest, PlaySpecification, WithApplication}
import services.MenuService

import scala.concurrent.Future

class MenuControllerSpec extends PlaySpecification with Mockito with JsonMatchers {
  sequential

/*  "测试 查询首页菜单" in new WithApplication() {
    val controller = app.injector.instanceOf[MenuController]
    val result = controller.listMenus().apply(FakeRequest())
    Json.parse(contentAsString(result)) must beEqualTo(Json.obj("1" -> "root",
      "8" -> "support", "2" -> "aboutUs", "4" -> "addProduct", "6" -> "categoryList",
      "3" -> "productManager", "5" -> "addCategory", "7" -> "comments"))
  }*/

/*  "测试 删除指定 图片" in new WithApplication() {
    val controller = app.injector.instanceOf[MenuController]

    //one way
    val request = FakeRequest("POST", "/api/menu/deleteImage").withJsonBody(Json.obj("menu" -> 4, "index" -> 1))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> 1))
    }

    //two way
    val rq = FakeRequest(
      POST,
      "/api/menu/deleteImage",
      new FakeHeaders(Seq("Content-type" -> "application/json")),
      Json.obj("menu" -> "1", "index" -> "1")
    )
    val rs = controller.deleteImage().apply(rq)
    contentAsString(rs) must contain("banner")
  }*/

/*  "测试 改变菜单图片顺序" in new WithApplication() {
    val request = FakeRequest("POST", "/api/menu/changeImgOrder/1/1/3")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "changeCompleted"))
    }
  }*/

/*  "测试 列出菜单子菜单" in new WithApplication() {
    val request1 = FakeRequest("GET", "/api/menu/findChildrenOfMenu/1")
    val request2 = FakeRequest("GET", "/api/menu/findChildrenOfMenu/3")
    val result1 = route(app, request1)
    val result2 = route(app, request2)
    result1.map { o =>
      val js1 = Json.parse(contentAsString(o))
      js1.as[JsObject].value must have size(3)
    }
    result2.map { o =>
      val js2 = Json.parse(contentAsString(o))
      js2.as[JsObject].value must have size(4)
    }
  }*/

  /*"测试 列出菜单子菜单" in new WithApplication() {
    val request = FakeRequest("POST", "/api/menu/changeMenuOrder/2/3")
    val result = route(app, request)
    result.map { o =>
      val js1 = Json.parse(contentAsString(o))
      js1.as[JsObject].value must have size(2)
    }
  }*/

/*  "测试 添加菜单" in new WithApplication() {
    val request = FakeRequest("POST", "/api/menu/addMenu").withJsonBody(Json.obj("name" -> "测试菜单",
      "parent" -> 1, "banner" -> Map("1" -> "222.jpg"), "desc" -> "测试用",
    "content" -> "测试用", "order" -> 4, "required" -> false))
    val result = route(app, request)
    result.map { o =>
      val js1 = contentAsString(o)
      js1 must /("name")
    }
  }*/

/*  "测试 删除菜单" in new WithApplication() {
    val request = FakeRequest("POST", "/api/menu/removeMenu/1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "delete complete"))
    }
  }*/
}

