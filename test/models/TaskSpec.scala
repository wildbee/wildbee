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
import helpers.ModelGenerator
import java.util.UUID


@RunWith(classOf[JUnitRunner])
class TaskSpec extends Specification with TestData with BeforeExample with ModelGenerator {
  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
    Statuses.insert(status1)
    Users.insert(user1)
    Workflows.insert(workflow1)
  }

  "Task model" should {
    "be able to add a new Tasks with an ID and throw error on conflicting IDs" in
    new WithApplication(fakeAppGen) {
      val uuids = for (i <- 0 until 10) yield uuidFactory.generate
      val tasks = uuids map (taskFactory.generateWithId(_))

      uuids map (Tasks.find(_)) must not (throwA[NoSuchElementException])
      Tasks.findAll.size === 10
      taskFactory.generateWithId(uuids(randomInt(0, 10))) must throwA[PSQLException]
    }

    "be able to add Tasks without an ID" in new WithApplication(fakeAppGen) {
      val tasks = for (i <- 0 until 10) yield taskFactory.generate
      Tasks.findAll.size === 10
    }

    "be able to delete Tasks" in new WithApplication(fakeAppGen){
      val tasks = for (i <- 0 until 10) yield taskFactory.generate
      tasks map (t => Tasks delete(t.id))
      Tasks.findAll.size === 0
      Tasks delete tasks(randomInt(0, 10)).id //must throwA[java.util.NoSuchElementException] ??
    }
  }
}