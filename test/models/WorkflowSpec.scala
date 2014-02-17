package models

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import org.postgresql.util.PSQLException

import play.api.test.WithApplication
import helpers.{ TestUtilities, ModelGenerator }

@RunWith(classOf[JUnitRunner])
class WorkflowSpec extends Specification with TestUtilities with BeforeExample with ModelGenerator {

  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
    resetModelGenerator()
  }

  /** TODO: Tests to add
   * Check that validation for deleting workflow that is being used as a dependency cannot be deleted
   */
  "Workflow model" should {
    "be able to add new workflows with specified ID and throw error on conflicting IDs" in
    new WithApplication(fakeAppGen) {
      val data =
        for {
          i <- 1 to 10
          u = uuidFactory.generate
          w = workflowFactory.generate(uuid = u)
        } yield (u, t)

      val uuids = data.map(_._1)
      val workflows = data.map(_._2)
      uuids map (Workflows.find(_)) must not(throwA[NoSuchElementException])
      Workflows.findAll.size === 10
      workflowFactory.generate(uuid = uuids(intBetween(0, 10))) must throwA[PSQLException]
    }

    "be able to add workflows without an ID" in new WithApplication(fakeAppGen) {
      val workflows = for (i <- 1 to 10) yield workflowFactory.generate
      Workflows.findAll.size === 10
    }

    "be able to delete workflows" in new WithApplication(fakeAppGen){
      val workflows = for (i <- 1 to 10) yield workflowFactory.generate
      val uuids = workflows map ( _.id )
      workflows map (w => Workflows delete (w.id))
      Workflows.findAll.size === 0
      uuids map (uuid => Transitions getLogic (uuid) must be empty)
    }
  }

}