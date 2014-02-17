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

  "User page" should {

    "be able to create a user from the user form" in new WithBrowser {
      running(fakeAppGen) {
        addUser(browser, port)
        Users.findAll.size === 1
      }
    }

    "be able to delete a user from the user viewing page" in new WithBrowser {
      running(fakeAppGen) {
        val email = addUser(browser, port)
        removeUser(browser, port, email)
        Users.findAll.size === 0
      }
    }

    "not allow entries of an empty username or email address"  in new WithBrowser {
      running(fakeAppGen) {
        val numUsers = Users.findAll.size
        addUser(browser, port, email="")
        browser.pageSource() must contain("Valid email required")
        addUser(browser, port, user="")
        browser.pageSource() must contain("This field is required")
        Users.findAll.size === numUsers
      }
    }

  }
}
