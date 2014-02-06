package helpers
import play.api.test.FakeApplication
import models._
import play.api._
import java.io.File
import org.clapper.classutil.ClassFinder
import models.traits.{ObserverCommand, Observable, Observer}
import java.util.UUID

trait TestUtilities {

  def fakeAppGen = FakeApplication(additionalConfiguration = Map(
    "db.default.url" -> "jdbc:postgresql://localhost/wildbeehivetest"),
    withGlobal = Some(new GlobalSettings() {
      override def onStart(app: Application) {
        Plugins.initializeNewPlugins(include="helpers.Start")
        Plugins.activate()
      }
      override def onStop(app: Application) {
        // Do nothing on stop
      }
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