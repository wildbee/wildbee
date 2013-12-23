package observers

import models.Entity
import models.traits.Observer
import models.traits.Observable



object TestObserver extends Observer {
  def update(s: Observable){
    println(s"I have observed a change in the '$s'")
  }
}