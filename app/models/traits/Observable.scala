package models.traits

import models.Entity

trait Observable {
  private var observers: List[Observer] = Nil

  def addObserver(o: Observer)(){
    if(observers forall(_.name != o.name))
      observers = o :: observers
  }

  def removeObserver(o: Observer){
    observers = observers filter( _ != o )
  }

  def getObservers: List[Observer] = observers

  def countObservers: Int = {
    observers.size
  }

  def notifyObservers(){
    observers foreach (_.update(this))
  }

}