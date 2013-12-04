package models

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import org.postgresql.util.PSQLException

import play.api.test.WithApplication
import helpers.{ TestUtilities, ModelGenerator }

@RunWith(classOf[JUnitRunner])
class PackageSpec extends Specification with TestUtilities with BeforeExample with ModelGenerator {

  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
    resetModelGenerator()
  }

  "Package model" should {
    "be able to add new workflows with specified ID and throw error on conflicting IDs" in
    new WithApplication(fakeAppGen) {
      val data =
        for {
          i <- 0 until 10
          u = uuidFactory.generate
          w = packageFactory.generate(uuid = u, withId = true)
        } yield (u, t)

      val uuids = data.map(_._1)
      val packages = data.map(_._2)
      uuids map (Packages.find(_)) must not(throwA[NoSuchElementException])
      Packages.findAll.size === 10
      packageFactory.generate(uuid = uuids(intBetween(0, 10))) must throwA[PSQLException]
    }
    "be able to add workflows without an ID" in new WithApplication(fakeAppGen) {
      val packages = for (i <- 0 until 10) yield packageFactory.generate
      Packages.findAll.size === 10
    }

    "be able to delete workflows" in new WithApplication(fakeAppGen){
      val packages = for (i <- 0 until 10) yield packageFactory.generate
      packages map (w => Workflows delete (w.id))
      Packages.findAll.size === 0
    }
  }

}