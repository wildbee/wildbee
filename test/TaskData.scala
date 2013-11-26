import org.specs2.mutable.{BeforeAfter}
import org.specs2.runner._
import play.api.test._
import play.api.test.Helpers._
import models.Tasks
import models.Users

trait TaskData extends BeforeAfter {

  lazy val UserOneID = Users.nameToId("Miles").toString

  def before() {
    running(FakeApplication()){
      println("Running Before ====================================================")
      Tasks deleteAll()
      Users deleteAll()
      Users.insert("Miles", "mtjandra@redhat.com")
    }
  }
  def after() {}
}