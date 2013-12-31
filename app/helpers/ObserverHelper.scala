package helpers

import java.io.File
import org.clapper.classutil.ClassFinder
import models.traits.Observer

object ObserverHelper {
  def getObserverNames: List[(String, String)]  = {
    val classpath = List(".") map (new File(_))
    println("CLASSPATH: " + classpath)
    val finder = ClassFinder(classpath)
    println("FINDER: " + finder)
    val classes = finder.getClasses  //Iterator[ClassInfo]
    println("CLASSES: " + classes)
    val plugins = (ClassFinder concreteSubclasses("models.traits.Observer", classes) toList)
                  .filter(!_.toString().startsWith("helpers")) //Filter out the test observers
    println("PLUGINS: " + plugins)
    val foo = (plugins map (x => Class.forName(x.toString()).newInstance().asInstanceOf[Observer]) )
    println("FOO: " + foo)
    foo map (o => (o.fullName, o.name))
  }
}