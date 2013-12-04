package helpers
import play.api.test.FakeApplication
import models._

trait TestUtilities {

  def fakeAppGen = FakeApplication(additionalConfiguration = Map(
  "db.default.url" -> "jdbc:postgresql://localhost/wildbeehivetest"))

  def clearDB() { //Order Matters
    Transitions.deleteAll
    Workflows.deleteAll
    Statuses.deleteAll
    Tasks.deleteAll
    Users.deleteAll
    Packages.deleteAll
  }
}