package integration

import helpers.{BrowserUtilities, TestUtilities}
import models.Users
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import org.specs2.specification.BeforeExample


@RunWith(classOf[JUnitRunner])
class UserIntegrationSpec extends Specification with BeforeExample
  with TestUtilities with BrowserUtilities  {

  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
  }

  "User" should {

    "be able to create a user from the user form" in new WithBrowser {
      running(fakeAppGen) {
        addUser(browser, port, "ScalaRules", "get@me.com")
        browser.$("#newUser").click()
        Users.findAll.size === 1
      }
    }

  }
}
