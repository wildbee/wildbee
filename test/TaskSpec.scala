import org.specs2._
import play.api.test.{FakeApplication}
import play.api.test.Helpers.{running}
import models.{Tasks, Users}
import org.specs2.specification.Example
import org.specs2.specification.ExampleFactory

/** Acceptance Driven? */
class TaskSpec extends Specification with TaskData { def is = s2"""
  Task model should
    be able to create a task                               $task1
    be able to delete a task                               $task2
  """

  sequential //I think tests run in parallel if this is not included
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

  /** Not sure how this works or what it does, but gets before/after working */
  override def exampleFactory = new ExampleFactory {
    def newExample(e: Example) = {
      val context = BeforeAfterExample(e)
      e.copy(body = () => context(e.body()) )
    }
  }

}



/* Behaviour Driven?
import org.specs2.mutable._
import play.api.test.{FakeApplication}
import play.api.test.Helpers.{running}
import models.{Tasks, Users}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TaskSpec extends Specification {
  "Task model" should {
    "be able to create a task" in {
      running(FakeApplication()){
      Tasks.deleteAll()
      Users.deleteAll()
      Users.insert("Miles", "mtjandra@redhat.com")
      Tasks.insert("TaskOne", Users.nameToId("Miles").toString)
      Tasks.findAll.size === 1
      }
    }
  }
}
*/