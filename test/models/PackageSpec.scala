package models

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import org.postgresql.util.PSQLException
import play.api.test.WithApplication
import helpers.{ TestUtilities, ModelGenerator }
import helpers.TestObserver
import models.traits.Observer

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
          w = packageFactory.generate(uuid = u)
        } yield (u, t)

      val uuids = data.map(_._1)
      val packages = data.map(_._2)
      uuids map (Packages.find(_)) must not(throwA[NoSuchElementException])
      Packages.findAll.size === 10
      packageFactory.generate(uuid = uuids(intBetween(0, 10))) must throwA[PSQLException]
    }

    "be able to add packages without an ID" in new WithApplication(fakeAppGen) {
      val packages = for (i <- 0 until 10) yield packageFactory.generate
      Packages.findAll.size === 10
    }

    "be able to delete packages" in new WithApplication(fakeAppGen){
      val packages = for (i <- 0 until 10) yield packageFactory.generate
      packages map (p => Packages delete (p.id))
      Packages.findAll.size === 0
    }

    "be able to add unique observer" in new WithApplication(fakeAppGen){
      val names = List("duplicate", "duplicate", "second", "duplicate")
      def uniqueObserverTest(names: List[String], observers: List[Observer] = List.empty) {
        names match {
          case(name :: rest ) =>
            val newObservers = TestObserver(name, false) :: observers
            Packages.addObserver(newObservers.head)
            Packages.countObservers === newObservers.groupBy(_.name).size
            uniqueObserverTest(rest, newObservers)
          case(Nil) => //Test Passed!
        }
      }
      uniqueObserverTest(names)
    }
    //TODO: Find out how to ret
    "notify observers when updated info" in new WithApplication(fakeAppGen){
      val names = List("duplicate", "duplicate", "second", "duplicate")
      val newOs = "NEW_OS"
      val observers = names map ( TestObserver(_, false) )
      observers foreach ( Packages.addObserver( _ ) )
      val pack = packageFactory.generate
      val newPack = NewPackage(pack.name, pack.task.toString(), //We need a one to make one...
          pack.creator.toString(), pack.assignee.toString(),
          pack.ccList, pack.status.toString(), newOs)
      Packages.update(pack.id, newPack)
      Packages.find(pack.id).osVersion === newOs
      Packages.getObservers foreach{ case o: TestObserver  => o.updated == true }
    }

  }
}