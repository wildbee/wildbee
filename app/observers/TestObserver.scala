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
    val classes = finder.getClasses                                                 //Iterator[ClassInfo]
    val plugins = ClassFinder.concreteSubclasses("models.traits.Observer", classes) //Iterator[ClassInfo]
    val dd = plugins filter (x => !x.toString().startsWith("helpers"))
    //dd foreach println
    val foo = (dd map (x => Class.forName(x.toString()).newInstance().asInstanceOf[Observer]) ).toList
    println("WHAT")
    foo foreach (o => println(o.name))
    println("WHAT")
    /*
    val hum = foo(0).setAccessible(true)
    println("HUM " + hum)
    foo(0).newInstance().asInstanceOf[Observer]*/
    //println("FOO " + foo)
  }
}

