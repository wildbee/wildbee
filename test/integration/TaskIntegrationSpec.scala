package integration

import helpers.{ModelGenerator, BrowserUtilities, TestUtilities}
import models.Users
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
    "no allow you to delete a task if something depends on it" in new WithBrowser {
      running(fakeAppGen) {
        val task = taskFactory.generate(name="Test")
        val packages = packageFactory.generate(taskId=task.id)
        browser.goTo(s"http://localhost:$port/task/${task.name}")
        browser.$("#deleteTask").click()
        browser.pageSource() must contain("depend on this entity")
      }
    }
  }
}
