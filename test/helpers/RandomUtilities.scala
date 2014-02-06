package helpers

import org.apache.commons.lang3.RandomStringUtils._
import scala.util.Random._
import models.traits.{ObserverCommand, Observable}
import java.util.UUID

trait RandomUtilities {

  var names: Set[String] = Set()

  def resetRandomUtilities(){
    names = Set()
  }

  def randEmail: String = {
    randString + "@" + randString
  }
  /** Generate a random alphanumeric string, and attempts not to product duplicates */
  def randString: String = {
    val name = randomAlphanumeric(intBetween(1, 10))
    if (names.contains(name)) randString
    else { names += name; name }
  }

  /** Give a random integer between lo inclusive and hi exclusive*/
  def intBetween(lo: Int, hi: Int) = {
    lo + nextInt().abs % (hi - lo)
  }

  /** Give a random integer between lo inclusive and hi inclusive*/
  def intInclusive(lo: Int, hi: Int) = {
    lo + nextInt().abs % (hi+1 - lo)
  }

  def printMe(name: String): (Observable,  UUID, ObserverCommand) => Unit = {
    def update(s: Observable, id: UUID, command: ObserverCommand): Unit = {
      println(s"I $name have observed a change in the '$s'")
    }
    update
  }
}
