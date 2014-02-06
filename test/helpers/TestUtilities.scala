package helpers
import play.api.test.FakeApplication
import models._
import play.api._

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

  /**
   * Clear your current databse
   * NOTE: ORDER MATTERS
   */
  def clearDB() {
    Packages.deleteAll
    Workflows.deleteAll
    Statuses.deleteAll
    Tasks.deleteAll
    Users.deleteAll
    Plugins.deleteAll
  }

  def restart(){
    Play.stop()
    Play.start(fakeAppGen)
  }

}