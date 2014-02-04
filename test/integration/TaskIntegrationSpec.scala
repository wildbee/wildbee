package integration

import helpers.{ModelGenerator, BrowserUtilities, TestUtilities}
import models.{Tasks, Users}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import org.specs2.specification.BeforeExample

class TaskIntegrationSpec extends Specification with BeforeExample
  with ModelGenerator with TestUtilities with BrowserUtilities {

  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
  }

  "Task page" should {
    "not allow you to delete a task if something depends on it" in new WithBrowser {
      running(fakeAppGen) {
        val task = taskFactory.generate(name="Test")
        val packages = packageFactory.generate(taskId=task.id)
        browser.goTo(s"http://localhost:$port/task/${task.name}")
        browser.$("#deleteTask").click()
        browser.pageSource() must contain("depend on this entity")
      }
    }

    "allow you to delete a task that is not dependent" in new WithBrowser {
      running(fakeAppGen){
        val tasks = for (i <- 1 to intInclusive(2,10)) yield taskFactory.generate
        val task = tasks(intBetween(0,tasks.size))
        val numTasks = Tasks.findAll.size
        removeTask(browser, port, task.name)
        Tasks.findAll.size === (numTasks - 1)
      }
    }
  }
}
