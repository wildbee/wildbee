package integration

import helpers.TestUtilities
import models.Users
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.test.TestServer

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
@RunWith(classOf[JUnitRunner])
class UserIntegrationSpec extends Specification with TestUtilities {

  "User" should {

    "be able to create a user from the user form" in new WithBrowser {
      running(fakeAppGen) {
        browser.goTo("http://localhost:" + port + "/user/new")
        browser.$("title").first.getText() must equalTo("User Form")
        browser.$("#name").text("scala")            //Fubd ud if bane abd ebter
        browser.$("#email").text("scala@scala.com") //Find id of email and enter
        browser.$("#newUser").click()
      }
    }
  }
}
