package controllers

import org.joda.time.DateTime
import org.specs2.matcher.JsonMatchers
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

class ArticleControllerSpec  extends PlaySpecification with Mockito with JsonMatchers {
  sequential

/*  "测试 展示文章   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/article/showArticle/1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("id" -> 1,
        "menu" -> 2,"title" -> "公司介绍","desc" -> "公司介绍",
        "content" -> "公司介绍","order" -> 1,"date" -> "2017-10-19 01:00:00"))
    }
  }*/

/*  "测试 文章列表   " in new WithApplication() {
    val request = FakeRequest("GET", "/api/article/showArticle/listArticles/2/-1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("1" -> "公司介绍",
      "2" -> "联系我们","4" -> "企业文化","3" -> "公司新闻","5" -> "品牌介绍"))
    }
  }*/

/*  "测试 添加文章   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/article/addArticle").withJsonBody(Json.obj("menu" -> 8,
      "title" -> "优惠活动", "desc" -> "优惠活动", "content" -> "优惠活动", "order" -> 1,
      "date" -> DateTime.now().toString("yyyy-MM-dd HH:mm:ss")))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("1" -> "公司介绍",
        "2" -> "联系我们","4" -> "企业文化","3" -> "公司新闻","5" -> "品牌介绍"))
    }
  }*/

/*  "测试 修改文章   " in new WithApplication() {
    val request = FakeRequest("POST", "/api/article/editArticle/1").withJsonBody(Json.obj("menu" -> 8,
      "title" -> "优惠活动1", "desc" -> "优惠活动1", "content" -> "优惠活动1", "order" -> 2,
      "date" -> DateTime.now().toString("yyyy-MM-dd HH:mm:ss")))
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "ok"))
    }
  }*/

/*  "测试 排序文章    " in new WithApplication() {
    val request = FakeRequest("POST", "/api/article/changeArticleOrder/1/1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("article" -> 2, "order" -> 4))
    }
  }*/

/*  "测试 删除文章    " in new WithApplication() {
    val request = FakeRequest("GET", "/api/article/removeArticle/1")
    val result = route(app, request)
    result.map { o =>
      Json.parse(contentAsString(o)) must beEqualTo(Json.obj("info" -> "delete complete"))
    }
  }*/
}
