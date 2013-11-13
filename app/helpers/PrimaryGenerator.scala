package helpers

import java.util.UUID

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 *
 */
trait PrimaryKeyGenerator {
  def newKey : UUID
  def fromString(uuid : String) : UUID
}

object DefaultPrimaryKeyGenerator extends PrimaryKeyGenerator {
  def newKey: UUID = java.util.UUID.randomUUID
  def fromString(uuid : String) : UUID = java.util.UUID.fromString(uuid)
}
