package observers

import models.traits.{Observer, Observable}
import java.util.UUID
import observers.commands.{ObserverCommand, ObserverCommands}
import ObserverCommands._

//Might have to use an Actor
case class Jira() extends Observer {
  override def update(s: Observable, id: UUID, command: ObserverCommand){

    command match {
      case New    => println(s"I $name have observed a change in the '$s', and received command $command")
      case Delete => this.untrack(id)
      case Edit   => println(s"I $name have observed a change in the '$s', and received command $command")
      case _      => println(s"I $name have observed a change in the '$s', and received command $command")
    }
  }
}