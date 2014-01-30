package models.traits

/**
 * Created by mtjandra on 1/30/14.
 */
trait ObserverCommand
trait ObserverCommands{
case object Delete extends ObserverCommand
case object Dummy extends ObserverCommand
}
