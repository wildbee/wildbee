package models.traits

import models.Entity
import java.util.UUID

/** Observable Description
 *  Anything that you want to allow an Observer to observe
 */
trait Observable {
  private var observers: List[Observer] = Nil

  def clearObservers() { observers = Nil }
  def countObservers: Int = observers.size
  def getObservers: List[Observer] = observers
  def setObservers(observers: List[Observer])() { this.observers = observers }

  def notifyObservers(id: UUID)() {
    observers foreach (o => if (o.isTracking(id)) o.update(this))
  }
  def removeObserver(o: String)() {
    observers = observers filter( _.name != o )
  }

  def addObserver(o: String, id: UUID)() {
    val observer = Class.forName(o).newInstance().asInstanceOf[Observer]
    observer.track(id)
    observers = observer :: observers
  }
  def addObservers(observerData: List[(String, UUID)])(){
    observerData foreach { case (observer, id) => addObserver(observer, id) }
  }


}