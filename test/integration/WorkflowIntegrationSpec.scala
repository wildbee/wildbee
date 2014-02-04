package integration

import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import helpers.{BrowserUtilities, TestUtilities, ModelGenerator}
import play.api.test.{WithBrowser, WithApplication}
import play.api.test.Helpers._
import models.{Workflows, Statuses}


class WorkflowIntegrationSpec extends Specification with BeforeExample
  with ModelGenerator with TestUtilities with BrowserUtilities {

  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
  }

  "Workflow page" should {
    "allow you to create a workflow" in new WithBrowser {
      running(fakeAppGen) {
        for (i <- 1 to 4) addStatus(browser, port, i.toString)
        browser.goTo(s"http://localhost:$port/workflow/new")
        browser.$("#name").text("1234")
        browser.$("#newWorkflow").click()
        Workflows.findAll.size === 1
      }
    }
    
    "not allow you to delete a workflow if something depends on it" in new WithBrowser {
      running(fakeAppGen) {
        val workflow = workflowFactory.generate(name="TEST")
        val task = taskFactory.generate(workflowId=workflow.id)
        browser.goTo(s"http://localhost:$port/workflow/${workflow.name}")
        browser.$("#deleteWorkflow").click()
        browser.pageSource() must contain("depend on this entity")
      }
    }
  }

}