package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import org.specs2.specification._
import play.api.test._
import play.api.test.Helpers._
import models._
import org.postgresql.util.PSQLException
import helpers.Config

@RunWith(classOf[JUnitRunner])
class TaskSpec extends Specification with TestData with BeforeExample {
  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
    Statuses.insert(status1)
    Users.insert(user1)
    Workflows.insert(workflow1)
  }

  "Task model" should {
    "be able to add a new Task with an ID" in new WithApplication(fakeAppGen) {
      Tasks.insertWithId(taskID, task1)
      Tasks.find(taskID) must not be null
      Tasks.findAll.size === 1
    }

    "be able to add Tasks without an ID" in new WithApplication(fakeAppGen) {
      Tasks.insert(task1)
      Tasks.findAll.size === 1
    }

    "not be able to add tasks with conflicting ids" in new WithApplication(fakeAppGen){
      Tasks.insertWithId(taskID, task1)
    	Tasks.insertWithId(taskID, task1) must throwA[PSQLException]
    }

    "be able to delete a Task" in new WithApplication(fakeAppGen){
      Tasks.insertWithId(taskID, task1)
      Tasks.insert(task2)
      Tasks.delete(taskID)
      Tasks.findAll.size === 1
      Tasks.find(taskID) must throwA[java.util.NoSuchElementException]
    }
  }
}