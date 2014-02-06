package observers

import java.util.UUID
import observers.commands.{ObserverCommand, ObserverCommands}
import ObserverCommands._
import models.traits.{Observable, Observer}

//Might have to use an Actor
case class Bugzilla() extends Observer  {
  override def update(s: Observable, id: UUID, command: ObserverCommand){
    command match {
      case New    => println(s"I $name have observed a change in the '$s', and received command $command")
      case Delete => {
        println(s"I $name have observed a change in the '$s', and received command $command")
        this.untrack(id)
      }
      case Edit   => println(s"I $name have observed a change in the '$s', and received command $command")
      case _      => println(s"I $name have observed a change in the '$s', and received command $command")
    }

  }
}

