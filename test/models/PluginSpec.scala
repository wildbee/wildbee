package models

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import org.postgresql.util.PSQLException

import play.api.test.WithApplication
import helpers.{ObserverHelper, TestUtilities, ModelGenerator}

@RunWith(classOf[JUnitRunner])
class PluginSpec extends Specification with TestUtilities with BeforeExample with ModelGenerator {
  sequential

  def before = new WithApplication(fakeAppGen) {
    clearDB()
    resetModelGenerator()
  }

  /** These tests are contrained by the  test observer limit*/
  "Plugin model" should {
    "be able to add new plugins" in new WithApplication(fakeAppGen) {
      val origSize = Plugins.findAll.size
      val data =
        for {
          i <- 1 to 10
          u = uuidFactory.generate
          p = pluginFactory.generate(uuid = u)
        } yield (u, p)
      val uuids = data.map(_._1)
      val plugins = data.map(_._2)
      Plugins.findAll.size === (origSize + 10)
      pluginFactory.generate(uuid = uuids(intBetween(0, 10))) must throwA[PSQLException]
    }

    "be able to add plugins without an ID" in new WithApplication(fakeAppGen) {
      val origSize = Plugins.findAll.size
      val plugins = for (i <- 1 to 10) yield pluginFactory.generate
      Plugins.findAll.size === ( origSize + 10 )
    }

    "be able to delete plugins" in new WithApplication(fakeAppGen) {
      val origSize = Plugins.findAll.size
      val plugins = for (i <- 1 to 10) yield pluginFactory.generate
      plugins map (p => Plugins delete (p.id))
      Plugins.findAll.size === origSize
    }
  }
}
