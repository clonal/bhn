package crawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

object CrawlerTest extends App {
  val browser: JsoupBrowser = JsoupBrowser.typed()
  final val detailTag = ".s-access-detail-page"
  final val resultTag = "#result_0"
  val asin = "B01HQ3XLZO"
  val asin1 = "B01J94T29W"
  val doc = browser.get(asin2Url(asin1))
//  for(url <- doc >> elementList(resultTag) >> element(detailTag) >> attr("href")) {
//    val productDoc = browser.get(url)
//
//  }
  val name = doc >> text("#productTitle")
  //描述
  val desc = (doc >> element(".feature")).outerHtml

  //属性
  val twister = doc >> element("#twister") >> elementList(".a-section .a-spacing-small")
  twister.foreach { t =>
    val key = t.attr("id")
    val value = t >> element(".a-row") >> text(".selection")
    println(s"key: $key, value: $value")
  }
  //价格

  val price = doc >> elementList("#price tr")
  if (price.length > 1) {
    val listPrice = price.head >> text(".a-text-strike")
    val p = price(1) >> text("#priceblock_ourprice")
    println(s"lp: $listPrice, p: $p")
  } else {
    val p = price.head >> text("#priceblock_ourprice")
    println(s"p: $p")
  }

  //图片
  val imgBlock = doc >> element("#imageBlock")
  val altImages = imgBlock >> elementList("#altImages li")

  val i = imgBlock >> elementList(".imageThumbnail img") >> attr("src")
  val i1 = imgBlock >> elementList(".imageThumbnail")
  println(i.mkString("\\n"))

  //评论


  def asin2Url(asin: String) = {
//    "https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=" + asin
    "https://www.amazon.com/dp/" + asin
  }


  println(s"...")
}
