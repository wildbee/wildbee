package helpers

import models.traits.{ObserverCommand, Observer, Observable}
import java.util.UUID


/** Test Observer
 *  A customizable observer for testing.'
 *  TODO: Figure out a way to get out of this
 * @param name name of your observer
 * @param updateFunc the update function of your observer
 */
case class TestObserver1(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }

}

case class TestObserver2(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}

case class TestObserver3(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }

}

case class TestObserver4(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}

case class TestObserver5(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}

case class TestObserver6(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}
case class TestObserver7(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}
case class TestObserver8(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}
case class TestObserver9(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}
case class TestObserver10(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}

case class TestObserver11(
  override val name: String,
  val updateFunc: (Observable,  UUID, ObserverCommand) => Unit) extends Observer {

  def update(s: Observable, id: UUID, command: ObserverCommand){
    updateFunc(s, id, command)
  }
}