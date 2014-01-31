package helpers

import org.apache.commons.lang3.RandomStringUtils._
import scala.util.Random._

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

}
