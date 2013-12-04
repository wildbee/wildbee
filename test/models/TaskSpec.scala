package models

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.specification._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models._
import org.postgresql.util.PSQLException
import helpers.Config
import helpers.ModelGenerator
import java.util.UUID
import helpers.TestUtilities

@RunWith(classOf[JUnitRunner])
class TaskSpec extends Specification with TestUtilities with BeforeExample with ModelGenerator {

  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
    resetModelGenerator()
  }

  "Task model" should {
    "be able to add a new Tasks with an ID and throw error on conflicting IDs" in
      new WithApplication(fakeAppGen) {
        val data =
          for {
            i <- 0 until 10
            u = uuidFactory.generate
            t = taskFactory.generate(uuid = u, withId = true)
          } yield (u, t)
        val uuids = data.map(_._1)
        val tasks = data.map(_._2)

        uuids map (Tasks.find(_)) must not(throwA[NoSuchElementException])
        Tasks.findAll.size === 10
        taskFactory.generate(uuid = uuids(intBetween(0, 10))) must throwA[PSQLException]
      }

    "be able to add Tasks without an ID" in new WithApplication(fakeAppGen) {
      val tasks = for (i <- 0 until 10) yield taskFactory.generate
      Tasks.findAll.size === 10
    }

    "be able to delete Tasks" in new WithApplication(fakeAppGen) {
      val tasks = for (i <- 0 until 10) yield taskFactory.generate()
      tasks map (t => Tasks delete (t.id))
      Tasks.findAll.size === 0
      Tasks delete tasks(intBetween(0, 10)).id //must throwA[java.util.NoSuchElementException] ??
    }

    /**
     * I want this to work, right now data is receiving an iterator
     * "This is for testing" in new WithApplication(fakeAppGen){
     * val data: Vector[UUID] =
     * for { u:UUID <- uuidFactory } yield (u: UUID)
     * println("DATA ::: " + data)
     * println(data.size)
     * }
     */
  }
}
