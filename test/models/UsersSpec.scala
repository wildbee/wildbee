package test.models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import org.specs2.specification._
import play.api.test._
import play.api.test.Helpers._
import models._
import org.postgresql.util.PSQLException
import helpers.Config
import helpers.TestUtilities

/**
 * Spec to test the Users model CRUD
 */
@RunWith(classOf[JUnitRunner])
class UsersSpec extends Specification with BeforeExample with TestUtilities {

  sequential

  def before = new WithApplication(fakeAppGen){
    clearDB()
  }

  "User model" should {

    "be able to add a new User" in new WithApplication(fakeAppGen) {
      Users.insert("name_of_user", "email@example.com")
    }

    "throw an exception when user with same email is added" in new WithApplication(fakeAppGen) {
      Users.insert("name_user", "email@example.com")
      Users.insert("name_user", "email@example.com") must throwA[PSQLException]
    }
  }
}
