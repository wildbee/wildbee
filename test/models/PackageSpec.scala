package models

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import org.postgresql.util.PSQLException
import play.api.test.WithApplication
import helpers.{ TestUtilities, ModelGenerator }
import models.traits.Observer
import helpers.ObserverHelper

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
          i <- 1 to 10
          u = uuidFactory.generate
          w = packageFactory.generate(uuid = u)
        } yield (u, t)

      val uuids = data.map(_._1)
      val packages = data.map(_._2)
      uuids map (Packages.find(_)) must not(throwA[NoSuchElementException])
      Packages.findAll.size === 10
      packageFactory.generate(uuid = uuids(intBetween(0, 10))) must throwA[PSQLException]
    }

    "be able to add packages without an ID" in new WithApplication(fakeAppGen) {
      val packages = for (i <- 1 to 10) yield packageFactory.generate
      Packages.findAll.size === 10
    }

    "be able to delete packages" in new WithApplication(fakeAppGen){
      val packages = for (i <- 1 to 10) yield packageFactory.generate
      packages map (p => Packages delete (p.id))
      Packages.findAll.size === 0
    }
  }
}