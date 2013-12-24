package helpers

import models.traits.Observer
import models.traits.Observable

case class TestObserver extends Observer {
  var updated = false
  val name = "Specs2 Observer"
  def update(s: Observable){
    this.updated = true

    println(s"I $name have observed a change in the '$s'")
  }
}