package utils

//import javax.crypto.Mac
//import javax.crypto.spec.SecretKeySpec

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import akka.http.scaladsl.model.Uri
import com.mohiva.play.silhouette.api.crypto.Base64

import scala.collection.immutable.{SortedMap, TreeMap}

object SignatureUtil {

  def genURL(serviceUrl: String, secretKey: String, algorithm: String, param: Map[String, String]): String =  {
    var parameters = TreeMap.empty[String, String] ++ param
    val formattedParameters = calculateStringToSignV2(parameters, serviceUrl)

    val signature = sign(formattedParameters, secretKey, algorithm)

    System.out.println("sign:" + signature);
    // Add signature to the parameters and display final results
    parameters += "Signature" -> Common.urlEncode(signature)
    val arys = calculateStringToSignV2(parameters,serviceUrl).split("\n");
    val url = "https://" + arys(1) + arys(2) + "?" + arys(3)
    System.out.println("url: " + url);
    url
  }

  /* If Signature Version is 2, string to sign is based on following:
      *
      *    1. The HTTP Request Method followed by an ASCII newline (%0A)
      *
      *    2. The HTTP Host header in the form of lowercase host,
      *       followed by an ASCII newline.
      *
      *    3. The URL encoded HTTP absolute path component of the URI
      *       (up to but not including the query string parameters);
      *       if this is empty use a forward '/'. This parameter is followed
      *       by an ASCII newline.
      *
      *    4. The concatenation of all query string components (names and
      *       values) as UTF-8 characters which are URL encoded as per RFC
      *       3986 (hex characters MUST be uppercase), sorted using
      *       lexicographic byte ordering. Parameter names are separated from
      *       their values by the '=' character (ASCII character 61), even if
      *       the value is empty. Pairs of parameter and values are separated
      *       by the '&' character (ASCII code 38).
      *
      */
  def calculateStringToSignV2(parameters: TreeMap[String, String], serviceUrl: String): String = {
    // Set endpoint value
    val endpoint = Uri(serviceUrl.toLowerCase())

    // Create flattened (String) representation
    val data = StringBuilder.newBuilder
    data.append("POST\n")
    data.append(endpoint.authority.host.toString())
    data.append("\n/Orders/2013-09-01")
    data.append("\n")
    //        data.append("AWSAccessKeyId=AKIAJSGPIMMF4OUVDDHA&Action=ListOrders&CreatedAfter=2017-10-31T16%3A00%3A00Z&MWSAuthToken=amzn.mws.16aa2629-9983-716f-667a-a6828be9f0c7&MarketplaceId.Id.1=ATVPDKIKX0DER&sellerId=A3IM69M5TI7T0P&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2017-11-02T08%3A41%3A38Z&Version=2013-09-01");
    parameters.foreach{ case (k, v) => data.append(s"$k=$v&") }
    data.toString().init
  }


  /*
  * Sign the text with the given secret key and convert to base64
  */
  def sign(data: String, secretKey: String, algorithm: String): String = {
    val mac = Mac.getInstance(algorithm)
    mac.init(new SecretKeySpec(secretKey.getBytes(Common.CharacterEncoding),
      algorithm))
    val  signature = mac.doFinal(data.getBytes(Common.CharacterEncoding))
    Base64.encode(signature)
  }


}
