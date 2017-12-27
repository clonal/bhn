import java.nio.file.Paths

import akka.Done
import akka.stream.IOResult
import akka.util.ByteString
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import reactivemongo.bson.{BSONArray, BSONDocument, BSONInteger}

import scala.concurrent.Future

object Test {
  def main(args: Array[String]): Unit = {
    import akka.NotUsed
    import akka.actor.ActorSystem
    import akka.stream.ActorMaterializer
    import akka.stream.scaladsl._

   /* final case class Author(handle: String)

    final case class Hashtag(name: String)

    final case class Tweet(author: Author, timestamp: Long, body: String) {
      def hashtags: Set[Hashtag] = body.split(" ").collect {
        case t if t.startsWith("#") => Hashtag(t.replaceAll("[^#\\w]", ""))
      }.toSet
    }

    val akkaTag = Hashtag("#akka")

    val tweets: Source[Tweet, NotUsed] = Source(
      Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
        Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
        Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
        Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
        Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
        Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
        Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
        Nil)

    implicit val system = ActorSystem("reactive-tweets")
    implicit val materializer = ActorMaterializer()

    val l = tweets.map(_.hashtags).reduce(_ ++ _)
    val t = l.mapConcat(identity)

    tweets
      .map(_.hashtags) // Get all sets of hashtags ...
      .reduce(_ ++ _) // ... and reduce them to a single set, removing duplicates across all tweets
      .mapConcat(identity) // Flatten the stream of tweets to a stream of hashtags
      .map(_.name.toUpperCase) // Convert all hashtags to upper case
      .runWith(Sink.foreach(println)) // Attach the Flow to a Sink that will finally print the hashtags

    // $FiddleDependency org.akka-js %%% akkajsactorstream % 1.2.5.1*/
/*    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher
    val source: Source[Int, NotUsed] = Source(1 to 100)
//
//    val done: Future[Done] = source.runForeach(i => println(i))(materializer)
//    done.onComplete(_ => system.terminate())

    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    val result: Future[IOResult] =
      factorials
        .map(num => ByteString(s"$num\n ,"))
        .runWith(FileIO.toPath(Paths.get("factorials.txt")))*/
    val date = "2010-01-01"
    val date1 = "2017-12-01T03:11:11.000+0000"
    val t = DateTime.parse(date).toString("yyyy-MM-dd HH:mm:ss")
    val t1 = DateTime.parse(date1, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    println(s"t: $t, t1: $t1")
  }
}
