package helpers
import java.util.UUID
/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 *
 */
object UUIDGenerator {
  def getUUID: UUID = {
    java.util.UUID.randomUUID()
  }
}
