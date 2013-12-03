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

    def randString = () => randomAlphanumeric(randomInt(1,10))

    /*
    def currentTimestamp: Timestamp = {
    	new Timestamp((new Date()).getTime())
    }*/

    /** Give a random integer between lo inclusive and hi exclusive*/
    def randomInt(lo: Int, hi: Int) =
      lo + nextInt().abs % (hi - lo)

    /** Random uuid generator */
  	val uuidFactory = new Generator[UUID]{
  		def generate = Config.pkGenerator.newKey
  	}

  	/** User Generator */
    def userFactory = new Generator[User] {
      def generate() = generate(randString(), randString() + "@" + randString(), uuidFactory.generate)
      def generate (name: String, email: String, uuid: UUID) ={
        val userId = Users.insert(User(uuid, name, email))
        Users find userId
      }
  	}

    /** Status Generator */
    def statusFactory = new Generator[Status] {
    	def generate() = generate(randString())
    	def generate(name: String, uuid: UUID = uuidFactory.generate) = {
    	  val statusId = Statuses.insert(Status(uuid, name))
    	  Statuses find statusId
    	}
    }

   /** Workflow Generator
    *  Also generates a random status since is required by workflow
    */
    def workflowFactory() = new Generator[Workflow] {
      def generate() = generate(randString(), uuidFactory.generate, uuidFactory.generate)
      def generate(name: String, statusId: UUID, uuid: UUID = uuidFactory.generate) = {
        val statusId = statusFactory.generate.id
        val workflowId = Workflows.insert(Workflow(uuid, name, statusId))
        Workflows find workflowId
      }
    }

    /** Task Generator
     *  1. Generates user
     *  2. Generates status
     *  3. Generates workflow
     *  Three additional generations requires by task
     */
    def taskFactory = new Generator[Task] {
      def generate() = generate(randString(), uuidFactory.generate)
      def generate(name: String, uuid: UUID, withId: Boolean = false) = {
	      val userId = userFactory.generate.id
	      val workflowId = workflowFactory.generate.id
	      val currentTime = Tasks.currentTimestamp
	      val taskId =
	        if (withId) Tasks.insertWithId(uuid, NewTask(name, userId.toString(), workflowId.toString()))
	        else Tasks.insert(Task(uuid, name, userId, workflowId, currentTime, currentTime))
	      Tasks find taskId
      }
      def generateWithId(uuid: UUID, name: String = randString()) = {
      	generate(name, uuid, true)
      }
    }

}