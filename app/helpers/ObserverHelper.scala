package helpers

import java.io.File
import org.clapper.classutil.ClassFinder
import models.traits.Observer

object ObserverHelper {
  def getObserverNames(includePackges: String = "observers"): List[(String, String)]  = {
    val ObserverPackage =  "models.traits.Observer"
    val classpath = List(".") map (new File(_))
    val finder = ClassFinder(classpath)                                             // org.clapper.classutil.ClassFinder@35427790
    val classes = finder.getClasses                                                 // Iterator[ClassInfo]
    val plugins = (ClassFinder concreteSubclasses(ObserverPackage, classes) toList) // List(observers.Jira, observers.Bugzilla)
                  .filter(_.toString().startsWith(includePackges))                // Filter out the test observers
    val names = ( plugins map (o => Class.forName(o.toString()).newInstance().asInstanceOf[Observer]) )
    names map (o => (o.path, o.name))
  }

  def mapIdToName(include: String = "observers"): Map[String, String]
    = getObserverNames(includePackges=include).toMap

}

