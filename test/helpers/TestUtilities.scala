package helpers
import play.api.test.FakeApplication
import models._
import play.api._

trait TestUtilities {

  val fakeAppGen = FakeApplication(additionalConfiguration = Map(
    "db.default.url" -> "jdbc:postgresql://localhost/wildbeehivetest"),
    withGlobal = Some(new GlobalSettings() {
      override def onStart(app: Application) { /** Don't run global settings */ }
    })
  )


  def clearDB() { //Order Matters
    Packages.deleteAll
    Workflows.deleteAll
    Statuses.deleteAll
    Tasks.deleteAll
    Users.deleteAll
    Plugins.deleteAll
  }

}