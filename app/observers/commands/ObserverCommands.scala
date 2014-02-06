package observers.commands

trait ObserverCommand
case object ObserverCommands {
  case object New extends ObserverCommand
  case object Edit extends ObserverCommand
  case object Delete extends ObserverCommand
}


