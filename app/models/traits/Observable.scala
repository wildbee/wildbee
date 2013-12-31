package models.traits

import models.Entity

trait Observable {
  private var observers: List[Observer] = Nil

  def addObserver(o: String)(){
    val observer = Class.forName(o).newInstance().asInstanceOf[Observer]
    observers = observer :: observers
  }

  def clearObservers() { observers = Nil }

  def countObservers: Int = observers.size

  def getObservers: List[Observer] = observers

  def notifyObservers() { observers foreach (_.update(this)) }

  def removeObserver(o: Observer){ observers = observers filter( _ != o ) }

  def setObservers(observers: List[Observer])() { this.observers = observers }

}