package controllers

import org.specs2.matcher.JsonMatchers
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONInteger}

class ProductController extends PlaySpecification with Mockito with JsonMatchers {
  sequential

/*    "测试 展示分类   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/product/listCategories")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("id" -> 1,
        "menu" -> 2,"title" -> "公司介绍","desc" -> "公司介绍",
        "content" -> "公司介绍","order" -> 1,"date" -> "2017-10-19 01:00:00"))
    }
  }*/


/*  "测试 查找分类   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/findCategory/1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("id" -> 1,
        "menu" -> 2,"title" -> "公司介绍","desc" -> "公司介绍",
        "content" -> "公司介绍","order" -> 1,"date" -> "2017-10-19 01:00:00"))
    }
  }*/

/*  "测试 添加分类   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/addCategory").withJsonBody(Json.obj("name" -> "书籍", "desc" -> "图书", "parent" -> 0))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("id" -> 1,
        "menu" -> 2,"title" -> "公司介绍","desc" -> "公司介绍",
        "content" -> "公司介绍","order" -> 1,"date" -> "2017-10-19 01:00:00"))
    }
  }*/

/*  "测试 修改分类   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/editCategory/7").withJsonBody(Json.obj("name" -> "书籍1", "desc" -> "图书1", "parent" -> 0))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 删除分类   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/removeCategory/7")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "delete complete"))
    }
  }*/

/*  "测试 产品列表   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/product/listProducts")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 产品列表   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/product/listProducts")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 查找产品   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/product/findProduct/1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 添加产品   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/addProduct").withJsonBody(Json.obj(
      "sku" -> Json.arr(Json.obj(
          "attributes" -> Json.arr(Json.obj("key" -> "aa", "value" -> "bb"), Json.obj("key" -> "cc", "value" -> "dd")),
          "name" -> "aaa",
          "content" -> "aaa1",
          "price" -> 1.0,
          "sellPrice" -> 2.0,
          "asin" -> "dddddd",
          "stock" -> 1,
          "show" -> 1,
          "images" -> Map("1" -> "aa.jpg")
        ),
        Json.obj(
          "attributes" -> Json.arr(Json.obj("key" -> "ee", "value" -> "ff")),
          "name" -> "bbb",
          "content" -> "bbb1",
          "price" -> 2.0,
          "sellPrice" -> 3.0,
          "asin" -> "ffffff",
          "stock" -> 55,
          "show" -> 1,
          "images" -> Map("1" -> "cc.jpg")
        )
      ),
      "name" -> "产品新",
      "desc" -> "新产品",
      "category" -> Array(1, 3),
      "amazonLink" -> "sss"
    ))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 修改产品   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/editProduct/7").withJsonBody(Json.obj(
      "name" -> "1231",
      "desc" -> "22121",
      "amazonLink" -> "12",
      "category" -> Array(2,3)
    ))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 删除产品   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/removeProduct/7")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 列出sku   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/product/listSkus")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 查找sku   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/product/findSku/5")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 查找skus   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/addSku/3").withJsonBody(Json.obj(
      "name" -> "new sku",
      "attributes" -> Array[Map[String, String]](Map("key" -> "key1", "value" -> "value1"), Map("key" -> "key2", "value" -> "value2")),
      "content" -> "content1",
      "price" -> 11.0,
      "sellPrice" -> 12.0,
      "asin" -> "asin1",
      "stock" -> 99,
      "show" -> 0,
      "images" -> Map("1" -> "2.jpg")
    ))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 修改sku   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/editSku/9").withJsonBody(Json.obj(
      "name" -> "new sku2",
      "attributes" -> Array[Map[String, String]](Map("key" -> "key3", "value" -> "value4"), Map("key" -> "key5", "value" -> "value6")),
      "content" -> "content4",
      "price" -> 112.0,
      "sellPrice" -> 122.0,
      "asin" -> "asin2",
      "stock" -> 992,
      "show" -> 1,
      "images" -> Map("1" -> "23.jpg")
    ))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 删除sku   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/product/removeSku/1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "delete completed!"))
    }
  }*/

}
