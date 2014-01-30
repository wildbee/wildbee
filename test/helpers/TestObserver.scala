package helpers

import models.traits.{ObserverCommand, Observer, Observable}
import java.util.UUID

case class ObserverOne() extends Observer {
  var updated = false
  def update(s: Observable, id: UUID, command: ObserverCommand){
    this.updated = true
    println(s"I $name have observed a change in the '$s'")
  }
}

case class ObserverTwo() extends Observer {
  var updated = false
  def update(s: Observable, id: UUID, command: ObserverCommand){
    this.updated = true
    println(s"I $name have observed a change in the '$s'")
  }
}