import org.specs2.mutable.{BeforeAfter}
import org.specs2.runner._
import play.api.test._
import play.api.test.Helpers._
import models.Tasks
import models.Users
import org.specs2.specification.Example
import org.specs2.specification.ExampleFactory

trait TaskData  {
  lazy val UserOneID = Users.nameToId("Miles").toString

  case class BeforeAfterExample(e: Example) extends BeforeAfter {
    def before = {
      running(FakeApplication()){
      Tasks deleteAll()
      Users deleteAll()
      Users.insert("Miles", "mtjandra@redhat.com")
      }
    }
    def after = {}
  }
}