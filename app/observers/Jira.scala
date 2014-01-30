package observers

import models.traits.{ObserverCommand, ObserverCommands, Observer, Observable}
import java.util.UUID

//Might have to use an Actor
case class Jira() extends Observer with ObserverCommands {
  def update(s: Observable, id: UUID, command: ObserverCommand){
    println(s"I $name have observed a change in the '$s', and received command $command")
    command match {
      case Delete => this.untrack(id)
      case Dummy =>  println("Dummt Command");
      case _ => println(s"ERROR $command")
    }
  }
}