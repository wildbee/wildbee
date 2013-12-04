package helpers

import models._

import java.util.Date
import java.sql.Timestamp
import java.util.UUID

import scala.util.Random.nextInt
import scala.collection.Iterator

import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

/**
 * Examples to get better idea of why map is needed
 *  for (x <- integers) yield x > 0
 *  val booleans = integers map {x => x > 0}
 *  val booleans = (x: Int => x > 0)
 *  val booleans = rand.nextInt > 0
 */
abstract class Generator[T] extends Iterator[T] {
  self =>
  private var toggle = 0
  def generate: T
  def next: T = self.generate
  def hasNext() = { //Generate 10 T's by default
    if (toggle <= 10) { toggle += 1; true }
    else { toggle = 0; false }

  }

  /* Maybe not necessary
  override def map[S](f: T => S): Generator[S] = new Generator[S] {
    def generate = f(self.generate)
  }
  def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
    def generate = f(self.generate).generate
  }*/
}

trait ModelGenerator extends {
  var names: Set[String] = Set()
  def randString: String = {
    val name = randomAlphanumeric(intBetween(1, 10))
    if (names.contains(name)) randString
    else { names += name; name }
  }

  /** Clear out information retained by the ModelGenerator */
  def resetModelGenerator() {
    names = Set()
  }

  /** Give a random integer between lo inclusive and hi exclusive*/
  def intBetween(lo: Int, hi: Int) =
    lo + nextInt().abs % (hi - lo)

  /** Random uuid generator */
  val uuidFactory = new Generator[UUID] {
    def generate = Config.pkGenerator.newKey
  }

  /**
   * User Generator
   *  Options
   *  uuid: Specify a UUID for your new user
   *  name: Specify a name for your new user
   *  email: Specify a email address for your new user
   *  withId: Choose if you want to use the withId implementation
   */
  val userFactory = new Generator[User] {
    def generate() = generate(email = (randString + "@" + randString))
    def generate(uuid: UUID = uuidFactory.generate, name: String = randString,
      email: String, withId: Boolean = false) = {
      val userId =
        if (withId) Users.insert(NewUser(name, email), uuid)
        else Users.insert(User(uuid, name, email))
      Users find userId
    }
  }

  /**
   * Status Generator
   *  Options
   *  uuid: Specify a UUID for your new status
   *  name: Specify a name for your new status
   *  withId: Choose if you want to use the withId implementation
   */
  val statusFactory = new Generator[Status] {
    def generate() = generate(uuid = uuidFactory.generate)
    def generate(uuid: UUID = uuidFactory.generate, name: String = randString, withId: Boolean = false) = {
      val statusId =
        if (withId) Statuses.insert(uuid, NewStatus(name), uuid)
        else Statuses.insert(Status(uuid, name))
      Statuses find statusId
    }
  }

  /**
   * Workflow Generator
   *  Default Usage Side Effects
   *  1. Generates status
   *  ===========================
   *  Options
   *  uuid: Specify a UUID for your new workflow
   *  name: Specify a name for your new workflow
   *  statusId: Specify which default status ID to use with your workflow
   *  withId: Choose if you want to use the withId implementation
   */
  val workflowFactory = new Generator[Workflow] {
    def generate() = generate(uuid = uuidFactory.generate)
    def generate(uuid: UUID = uuidFactory.generate, name: String = randString,
      statusId: UUID = statusFactory.generate.id, withId: Boolean = false) = {
      val workflowId =
        if (withId) Workflows.insert(NewWorkflow(name, List(statusId.toString())), uuid)
        else Workflows.insert(Workflow(uuid, name, statusId))
      Workflows find workflowId
    }
  }

  /**
   * Task Generator
   *  Default Usage Side Effects
   *  1. Generates user
   *  2. Generates status
   *  3. Generates workflow
   *  ========================
   *  Options
   *  uuid: Specify a UUID for your new task
   *  name: Specify a name for your new task
   *  userId: Specify which user the task should be assigned to by a user id
   *  workflowId: Specify which workflow the task should be assigned to by using a workflow if
   *  currentTime: Specify the timestamp for your task
   *  withId: Choose if you want to use the withId implementation
   */
  val taskFactory = new Generator[Task] {
    def generate(): Task = generate(uuid = uuidFactory.generate)
    def generate(
      uuid: UUID = uuidFactory.generate, name: String = randString,
      userId: UUID = userFactory.generate.id, workflowId: UUID = workflowFactory.generate.id,
      currentTime: Timestamp = Tasks.currentTimestamp, withId: Boolean = false): Task = {
      val taskId =
        if (withId) Tasks.insert(NewTask(name, userId.toString(), workflowId.toString()), uuid)
        else Tasks.insert(Task(uuid, name, userId, currentTime, workflowId, currentTime))
      Tasks.find(taskId)
    }
  }

}
