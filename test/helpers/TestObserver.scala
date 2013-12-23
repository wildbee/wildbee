package helpers

import models.traits.Observer
import models.traits.Observable

case class TestObserver(name: String, var updated: Boolean) extends Observer {
  def update(s: Observable){
    this.updated = true

    println(s"I $name have observed a change in the '$s'")
  }
}