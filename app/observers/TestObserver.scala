package observers

import models.Entity
import models.traits.Observer
import models.traits.Observable
import org.clapper.classutil.ClassFinder
import java.io.File
import scala.reflect.runtime.{universe => ru}
import scala.reflect.api.TypeTags

case class TestObserver extends Observer {
  val name = "TestObserver"
  def update(s: Observable){
    println(s"I have observed a change in the '$s'")
    init()
  }

  //WARNING: concreteSubclasses can chew up a lot of heap space temporarily, if called with a large classpath
  def init() {
    val classpath = List(".").map(new File(_))
    val finder = ClassFinder(classpath)
    val classes = finder.getClasses  //Iterator[ClassInfo]
    val plugins = (ClassFinder concreteSubclasses("models.traits.Observer", classes) toList)
                  .filter(!_.toString().startsWith("helpers")) //Filter out the test observers
    val foo = (plugins map (x => Class.forName(x.toString()).newInstance().asInstanceOf[Observer]) )//.toList
    foo foreach (o => println(o.name))
  }
}

