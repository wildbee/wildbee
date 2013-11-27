package test.models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models._
import org.postgresql.util.PSQLException

/**
 * Spec to test the Users model CRUD
 */
@RunWith(classOf[JUnitRunner])
class UsersSpec extends Specification {

//  TODO: dangerous since it deletes in the main database, not the test one
//  implicit val afterContext = new After { def after = scala.slick.session.Database.forURL("jdbc:postgresql://localhost/wildbeehivetest") withSession {Users.deleteAll} }
//  implicit val beforeContext = new Before { def before = scala.slick.session.Database.forURL("jdbc:postgresql://localhost/wildbeehivetest") withSession {Users.deleteAll} }

  def fakeAppGen = FakeApplication(additionalConfiguration = Map(
    "db.default.url" -> "jdbc:postgresql://localhost/wildbeehivetest"))

  "User model" should {

    "should be able to add a new User" in new WithApplication(fakeAppGen) {
      Users.deleteAll
      Users.insert("name_of_user", "email@example.com")
    }

    "throw an exception when user with same email is added" in new WithApplication(fakeAppGen) {
      Users.deleteAll
      Users.insert("name_user", "email@example.com")
      Users.insert("name_user", "email@example.com") must throwA[PSQLException]
    }
  }
}
