package helpers
import models._
import scala.util.Random.nextInt
import java.util.UUID
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

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

trait ModelGenerator {

  //def main(args: Array[String])  {

    def randString = () => randomAlphanumeric(integer(1,10))

    /** Give a random integer between lo inclusive and hi exclusive*/
    def integer(lo: Int, hi: Int) =
      lo + nextInt().abs % (hi - lo)

    /** Random uuid generator */
  	val uuids = new Generator[UUID]{
  		def generate = Config.pkGenerator.newKey
  	}

  	/** User Generator */
    def user = new Generator[User] {
      def generate() = generate(randString(), randString() + "@" + randString(), uuids.generate)
      def generate (name: String, email: String, uuid: UUID) ={
        val userId = Users.insert(User(uuid, name, email))
        Users find userId
      }
  	}

    /** Status Generator */
    def status = new Generator[Status] {
    	def generate() = generate(randString())
    	def generate(name: String, uuid: UUID = uuids.generate) = {
    	  val statusId = Statuses.insert(Status(uuid, name))
    	  Statuses find statusId
    	}
    }

   /** Workflow generator
    *  Also generates a random status since is required by workflow
    */
    def workflow() = new Generator[Workflow] {
      def generate() = generate(randString(), uuids.generate, uuids.generate)
      def generate(name: String, statusId: UUID, uuid: UUID = uuids.generate) = {
        val statusId = status.generate.id
        val workflowId = Workflows.insert(Workflow(uuid, name, statusId))
        Workflows find workflowId
      }
    }


    /*
    def task() = new Generator[Task] {
      def generate() = generate(uuids.generate, randString(), uuids.generate)
      def generate(uuid: UUID, name: String, statusId: UUID) = {
        val statusId = status.generate.id
        val workflowId = Workflows.insert(Workflow(uuid, name, statusId))
        Workflows find workflowId
      }
    }*/

}