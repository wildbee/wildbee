package controllers
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import scala.io.Source


@RunWith(classOf[JUnitRunner])
class ActionSanitySpec extends Specification {

  /** Check if response code 200 returned */
  def checkIfUrlAccessible(url: String): Unit = {
    val appRoute = route(FakeRequest(GET, url)).get
    status(appRoute) must equalTo(OK)
    contentType(appRoute) must beSome.which(_ == "text/html")
  }

  "Application" should {

    //Searches conf/routes file for GET urls and check if they return response code 200
    for(line <- Source.fromFile("conf/routes").getLines()){
      if (line.startsWith("GET") && !line.contains(":") && !line.contains("*")){
        val url = (line.split(" +")(1))
        ("render the page " + url) in new WithApplication { checkIfUrlAccessible(url) }
      }
    }

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/NaN")) must beNone
    }

  }
}
