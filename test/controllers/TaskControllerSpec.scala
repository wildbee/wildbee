package controllers

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import org.postgresql.util.PSQLException

import play.api.test.Helpers._
import play.api.test.{FakeApplication,FakeRequest, WithApplication}
import helpers.{ TestUtilities, ModelGenerator }

@RunWith(classOf[JUnitRunner])
class TaskControllerSpec extends Specification with TestUtilities with BeforeExample with ModelGenerator {

  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
    resetModelGenerator()
  }

  "Task controller" should {

    "be able to show you the task index" in new WithApplication {
      val tasks =  route(FakeRequest(GET, "/tasks")).get
      status(tasks) === OK
    }
  }

}