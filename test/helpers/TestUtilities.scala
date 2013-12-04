package helpers
import play.api.test.FakeApplication
import models.Statuses
import models.Tasks
import models.Users
import models.Workflows

trait TestUtilities {

  def fakeAppGen = FakeApplication(additionalConfiguration = Map(
  "db.default.url" -> "jdbc:postgresql://localhost/wildbeehivetest"))

  def clearDB() { //Order Matters
    Workflows.deleteAll
    Statuses.deleteAll
    Tasks.deleteAll
    Users.deleteAll
  }
}