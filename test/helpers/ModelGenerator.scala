package helpers
import models._
import scala.util.Random.nextInt
import java.util.UUID
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import java.sql.Timestamp
import java.util.Date
import scala.collection.TraversableOnce
import scala.collection.Iterator
import scala.util.continuations._

abstract class Generator[T] extends Iterator[T] {
  self =>
  private var toggle = false
  def generate: T
  def next =  generate
	def hasNext() = { toggle = !toggle; println(toggle); toggle }
  override def map[S](f: T => S): Generator[S] = new Generator[S] {
    def generate = f(self.generate)
  }
  def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
    def generate = f(self.generate).generate
  }
}

/** Notes
 *  TODO: If you generate too many things you'll have name/uuid collisions
 *  TODO: Be able to generate models with something like
 *  val uuids, tasks = for {
 *    i <- 0 until 10
 *    u <- uuidFactory
 *    t <- taskFactory.withId(u)
 *  } yield (u, t)
 */
trait ModelGenerator extends {
		var names: Set[String] = Set()
		def randString: String = {
		  val name = randomAlphanumeric(intBetween(1,10))
		  if (names.contains(name)) randString
		  else { names += name ; name }
		}

		def resetModelGenerator() {
		  names = Set()
		}


    /** Give a random integer between lo inclusive and hi exclusive*/
    def intBetween(lo: Int, hi: Int) =
      lo + nextInt().abs % (hi - lo)

    /** Random uuid generator */
  	val uuidFactory = new Generator[UUID]{
  		def generate = Config.pkGenerator.newKey
  	}

  	/** User Generator
  	 *  Options
  	 *  uuid: Specify a UUID for your new user
  	 *  name: Specify a name for your new user
  	 *  email: Specify a email address for your new user
  	 *  withId: Choose if you want to use the withId implementation
  	 */
    def userFactory = new Generator[User] {
      def generate() = generate(email=(randString + "@" + randString))
      def generate (uuid: UUID=uuidFactory.generate,name: String=randString,
          email: String, withId: Boolean=false) = {
        val userId =
          if (withId) Users.insertWithId(uuid, NewUser(name, email))
          else Users.insert(User(uuid, name, email))
        Users find userId
      }
  	}

    /** Status Generator
     *  Options
     *  uuid: Specify a UUID for your new status
     *  name: Specify a name for your new status
     *  withId: Choose if you want to use the withId implementation
     */
    def statusFactory = new Generator[Status] {
    	def generate() = generate(uuid=uuidFactory.generate)
    	def generate(uuid: UUID = uuidFactory.generate, name: String=randString, withId: Boolean=false) = {
    	  val statusId =
    	    if (withId) Statuses.insertWithId(uuid, NewStatus(name))
    	    else Statuses.insert(Status(uuid, name))
    	  Statuses find statusId
    	}
    }

   /** Workflow Generator
    *  Default Usage Side Effects
    *  1. Generates status
    *  ===========================
    *  Options
    *  uuid: Specify a UUID for your new workflow
    *  name: Specify a name for your new workflow
    *  statusId: Specify which default status ID to use with your workflow
    *  withId: Choose if you want to use the withId implementation
    */
    def workflowFactory() = new Generator[Workflow] {
      def generate() = generate(uuid=uuidFactory.generate)
      def generate(uuid: UUID = uuidFactory.generate, name: String=randString,
          statusId: UUID=statusFactory.generate.id, withId: Boolean=false) = {
        val workflowId =
          if(withId) Workflows.insertWithId(uuid, NewWorkflow(name, List(statusId.toString())))
          else Workflows.insert(Workflow(uuid, name, statusId))
        Workflows find workflowId
      }
    }

    /** Task Generator
     *  Default Usage Side Effects
     *  1. Generates user
     *  2. Generates status
     *  3. Generates workflow
     *	========================
     *  Options
     *  uuid: Specify a UUID for your new task
     *  name: Specify a name for your new task
     *  userId: Specify which user the task should be assigned to by a user id
     *  workflowId: Specify which workflow the task should be assigned to by using a workflow if
     *  currentTime: Specify the timestamp for your task
     *  withId: Choose if you want to use the withId implementation
     */
    def taskFactory = new Generator[Task] {
      def generate(): Task = generate(uuid=uuidFactory.generate)
      def generate(
          uuid: UUID = uuidFactory.generate, name: String = randString,
          userId: UUID = userFactory.generate.id, workflowId: UUID = workflowFactory.generate.id,
          currentTime: Timestamp = Tasks.currentTimestamp, withId: Boolean = false): Task = {
	      val taskId =
	        if (withId) Tasks.insertWithId(uuid, NewTask(name, userId.toString(), workflowId.toString()))
	        else Tasks.insert(Task(uuid, name, userId, workflowId, currentTime, currentTime))
	      Tasks find taskId
      }
    }

}