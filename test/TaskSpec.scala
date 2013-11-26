import org.specs2._
import play.api.test.{FakeApplication}
import play.api.test.Helpers.{running}
import models.{Tasks, Users}
import org.specs2.specification.Example
import org.specs2.specification.ExampleFactory
import org.specs2.specification.BeforeAfter

class TaskSpec extends Specification  { def is =                              s2"""
  Task mode should
    be able to create a task                               $task1
    be able to delete a task                               $task2

                                                                                """
  sequential
  lazy val UserOneID = Users.nameToId("Miles").toString
  def task1 = {
    running(FakeApplication()){
      Tasks.insert("TaskOne", Users.nameToId("Miles").toString)
      Tasks.findAll.size === 1
    }
  }
  def task2 = {
      running(FakeApplication()){
      Tasks.insert("TaskOne", UserOneID)
      Tasks.delete("TaskOne")
      Tasks.findAll.size === 0
      }
  }

  case class BeforeAfterExample(e: Example) extends BeforeAfter {
    def before = {
      println("Running Before ====================================================")
      running(FakeApplication()){
      Tasks deleteAll()
      Users deleteAll()
      Users.insert("Miles", "mtjandra@redhat.com")
    }
    }
    def after = {}
  }

  override def exampleFactory = new ExampleFactory {
    def newExample(e: Example) = {
      val context = BeforeAfterExample(e)
      e.copy(body = () => context(e.body()))
    }
  }

}
