package helpers

import models._

import java.util.Date
import java.sql.Timestamp
import java.util.UUID

import scala.util.Random.nextInt
import scala.collection.Iterator

import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import models.traits.{ObserverCommand, Observer, Observable}
;

/**
 * Examples to get better idea of why map is needed
 *  for (x <- integers) yield x > 0
 *  val booleans = integers map {x => x > 0}
 *  val booleans = (x: Int => x > 0)
 *  val booleans = rand.nextInt > 0
 */
abstract class Generator[Model, NewModel] extends Iterator[Model] {
  self =>
  private var toggle = 0
  def generate: Model
  def next: Model = self.generate
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
    numObservers = 0
  }

  /** Give a random integer between lo inclusive and hi exclusive*/
  def intBetween(lo: Int, hi: Int) =
    lo + nextInt().abs % (hi - lo)

  /** Random uuid generator */
  object uuidFactory extends Generator[UUID, UUID] {
    def generate = Config.pkGenerator.newKey
  }

  /**
   * Observer Generator
   *  ========================
   *  Options
   *  name: Specify a name for your observser
   *  update: Specify an update function for your observer
   *          Default function is a printMe function that just print out that our obsverer has observerd something
   */
  var numObservers = 0
  object observerFactory extends Generator[Observer, Observer] {

    /** Default Observer update function */
    private def printMe(name: String): (Observable,  UUID, ObserverCommand) => Unit = {
      def update(s: Observable, id: UUID, command: ObserverCommand): Unit = {
        println(s"I $name have observed a change in the '$s'")
      }
      update
    }
    private val defaultFunc = printMe(randString)

    /** Generate an observer
     *  Doing this to avoid macros?
     *  A better solution is required.
     */
    private def genObserver(name: String, f: (Observable,  UUID, ObserverCommand) => Unit,
            num: Int): Observer = num match {
      case 1 => TestObserver1(name, f)
      case 2 => TestObserver2(name, f)
      case 3 => TestObserver3(name, f)
      case 4 => TestObserver4(name, f)
      case 5 => TestObserver5(name, f)
      case 6 => TestObserver6(name, f)
      case 7 => TestObserver7(name, f)
      case 8 => TestObserver8(name, f)
      case 9 => TestObserver9(name, f)
      case 10 => TestObserver10(name, f)
      case 11 => TestObserver11(name, f)
      case x => throw new IllegalArgumentException(s"Past your observer limit $x")
    }

    def generate(): Observer = generate(name = randString)
    def generate( name: String = randString,
      update: Option[(Observable,  UUID, ObserverCommand) => Unit] = None) = {
      numObservers += 1
      update match {
       case None => genObserver(name, printMe(name), numObservers)
       case Some(f) => genObserver(name, f, numObservers)
      }
    }
  }

  /**
   * User Generator
   *  Options
   *  uuid: Specify a UUID for your new user
   *  name: Specify a name for your new user
   *  email: Specify a email address for your new user
   */
  object userFactory extends Generator[User, NewUser] {
    def generate() = generate(email = (randString + "@" + randString))
    def generate(uuid: UUID = uuidFactory.generate,
        name: String = randString, email: String = (randString + "@" + randString)) = {
      val userId = Users.insert(User(uuid, name, email))
      Users.find(userId) match {
        case Some(user) => user
        case None => throw new IllegalArgumentException
      }
    }
  }

  /**
   * Status Generator
   *  Options
   *  uuid: Specify a UUID for your new status
   *  name: Specify a name for your new status
   */
  object statusFactory extends Generator[Status, NewStatus] {
    def generate() = generate(uuid = uuidFactory.generate)
    def generate(uuid: UUID = uuidFactory.generate, name: String = randString) = {
      val statusId = Statuses.insert(Status(uuid, name))
      Statuses.find(statusId) match {
        case Some(status) => status
        case None => throw new IllegalArgumentException
      }
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
   */
  object workflowFactory extends Generator[Workflow, NewWorkflow] {
    def generate() = generate(uuid = uuidFactory.generate)
    def generate(uuid: UUID = uuidFactory.generate, name: String = randString, statusId: UUID = statusFactory.generate.id) = {
      val workflowId =  Workflows.insert(Workflow(uuid, name, statusId))
      Workflows.find(workflowId) match {
        case Some(workflow) => workflow
        case None => throw new IllegalArgumentException
      }
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
   */

  object taskFactory extends Generator[Task, NewTask] {
    def generate(): Task = generate(uuid = uuidFactory.generate)
    def generate(
      uuid: UUID = uuidFactory.generate, name: String = randString,
      userId: UUID = userFactory.generate.id, workflowId: UUID = workflowFactory.generate.id,
      currentTime: Timestamp = Tasks.currentTimestamp): Task = {
      val taskId = Tasks.insert(Task(uuid, name, userId, currentTime, workflowId, currentTime))
      Tasks.find(taskId) match {
        case Some(task) => task
        case None => throw new IllegalArgumentException
      }
    }
  }

  /**
   * Package Generator
   *  Default Usage Side Effects
   *  1. Generates user
   *  2. Generates status
   *  3. Generates workflow
   *  ========================
   *  Options
   *  uuid: Specify a UUID for your new package
   *  name: Specify a name for your new package
   *  taskID: Specify which task the package should be assigned to by a task id
   *  creatorId: Specify which user the package should mark as created by a user id
   *  asigneeId: Specify which user the  package should mark as assigned by a user id
   *  ccList: Specify the CC List for the package
   *  statusId: Specify the status of the packae
   *  osVersion: Specify the os version of the package
   *  currentTime: Specify the timestamp for your task
   */
  object packageFactory extends Generator[Package, NewPackage] {
    def generate(): Package = generate(uuid = uuidFactory.generate)
    def generate(
      uuid: UUID = uuidFactory.generate, name: String = randString,
      taskId: UUID = taskFactory.generate.id, creatorId: UUID = userFactory.generate.id,
      asigneeId: UUID = userFactory.generate.id, ccList: String = randString, statusId: UUID = statusFactory.generate.id,
      osVersion: String = randString, currentTime: Timestamp = Packages.currentTimestamp): Package = {
      val packageId = Packages.insert(Package(uuid, name, taskId, creatorId, asigneeId, ccList, statusId, osVersion, currentTime, currentTime))
      Packages.find(packageId) match {
        case Some(pkg) => pkg
        case None => throw new IllegalArgumentException
      }
    }
    //TODO: Make this return a named function
    def modifyModel(pack: Package): (String, String, String, String, String, String, String) => NewPackage = {
      def modifyModelD  (name: String = pack.name, task:String = Tasks.idToName(pack.task),
          creator:String = Users.idToName(pack.creator), assignee: String = Users.idToName(pack.assignee),
          ccList: String = pack.ccList, status:String = Statuses.idToName(pack.status),
          osVersion: String = pack.osVersion): NewPackage = {
            NewPackage(name, task, creator, assignee, ccList, status ,osVersion)
      }
      modifyModelD
    }
  }

  /**
   * Plugin Generator
   *  Default Usage Side Effects
   *  1. Generates a packages
   *  2. Generates a Observer
   *  ========================
   *  Options
   *  uuid: Specify a UUID for your new plugin
   *  name: Specify a name for your new plugin
   *  path: Specify a path to your observer
   *  packId: Specify what package to track
   */
  object pluginFactory extends Generator[Plugin, NewPlugin] {
    def generate(): Plugin = generate(uuid = uuidFactory.generate)
    def generate( uuid: UUID = uuidFactory.generate, name: String = randString,
                  path: String = observerFactory.generate.path, packId:UUID = packageFactory.generate.id): Plugin = {
      val pluginId = Plugins.insert(Plugin(uuid, name, path, Some(packId)))
      Plugins.find(pluginId) match {
        case Some(plugin) => plugin
        case None => throw new IllegalArgumentException
      }
    }
  }
}
