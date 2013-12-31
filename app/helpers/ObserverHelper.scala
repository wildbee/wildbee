package helpers

import java.io.File
import org.clapper.classutil.ClassFinder
import models.traits.Observer


object ObserverHelper {
  def getObserverNames: List[(String, String)]  = {
    // CLASSPATH: List(.)
    // FINDER: org.clapper.classutil.ClassFinder@35427790
    // CLASSES: non-empty iterator => Iterator[ClassInfo]
    // PLUGINS: List(observers.Jira, observers.Bugzilla)
    // Names: List(Jira(), Bugzilla())
    val ObserverPacakge =  "models.traits.Observer"
    val classpath = List(".") map (new File(_))
    val finder = ClassFinder(classpath)
    val classes = finder.getClasses
    val plugins = (ClassFinder concreteSubclasses(ObserverPacakge, classes) toList)
                  .filter(!_.toString().startsWith("helpers")) //Filter out the test observers
    val names = ( plugins map (x => Class.forName(x.toString()).newInstance().asInstanceOf[Observer]) )
    names map (o => (o.fullName, o.name))
  }

  def mapIdToName = getObserverNames.toMap

}

