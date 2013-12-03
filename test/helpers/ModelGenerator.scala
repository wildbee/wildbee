package helpers
import models._
import scala.util.Random.nextInt
import java.util.UUID
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import java.sql.Timestamp
import java.util.Date
import scala.collection.TraversableOnce

trait Generator[+T] {
   /** An alias for "this" */
  self =>

  /** Alias self is a solution to refer to generate method below */
  def generate: T

  /** Allow for single 'for' loop */
  def map[S](f: T => S): Generator[S] = new Generator[S] {
    def generate = f(self.generate)
  }

  /** Allow for 'multi-for' loop
   * 1) Generate a random value of type T with self.generate
   * 2) Generate a generator with new domain S with the function f
   * 3) Pick a random element from S domain with generate
   */
  def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
    def generate = f(self.generate).generate
  }
}

trait ModelGenerator extends {

    def randString = () => randomAlphanumeric(randomInt(1,10))

    def currentTimestamp: Timestamp = {
    	new Timestamp((new Date()).getTime())
    }

    /** Give a random integer between lo inclusive and hi exclusive*/
    def randomInt(lo: Int, hi: Int) =
      lo + nextInt().abs % (hi - lo)

    /** Random uuid generator */
  	val uuidFactory = new Generator[UUID]{
  		def generate = Config.pkGenerator.newKey
  	}

  	/** User Generator */
    def user = new Generator[User] {
      def generate() = generate(randString(), randString() + "@" + randString(), uuidFactory.generate)
      def generate (name: String, email: String, uuid: UUID) ={
        val userId = Users.insert(User(uuid, name, email))
        Users find userId
      }
  	}

    /** Status Generator */
    def status = new Generator[Status] {
    	def generate() = generate(randString())
    	def generate(name: String, uuid: UUID = uuidFactory.generate) = {
    	  val statusId = Statuses.insert(Status(uuid, name))
    	  Statuses find statusId
    	}
    }

   /** Workflow Generator
    *  Also generates a random status since is required by workflow
    */
    def workflow() = new Generator[Workflow] {
      def generate() = generate(randString(), uuidFactory.generate, uuidFactory.generate)
      def generate(name: String, statusId: UUID, uuid: UUID = uuidFactory.generate) = {
        val statusId = status.generate.id
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
    val taskFactory = new Generator[Task] {
      def generate() = generate(randString(), uuidFactory.generate)
      def generate(name: String, uuid: UUID, withId: Boolean = false) = {
	      val userId = user.generate.id
	      val workflowId = workflow.generate.id
	      val taskId =
	        if (withId) Tasks.insertWithId(uuid, NewTask(name, userId.toString(), workflowId.toString()))
	        else Tasks.insert(Task(uuid, name, userId, workflowId, currentTimestamp, currentTimestamp))
	      Tasks find taskId
      }
      def generateWithId(uuid: UUID, name: String = randString()) = {
      	generate(name, uuid, true)
      }
    }

}