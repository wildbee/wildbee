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

  /**
   * Checks if the string uuid is in valid uuid format.
   * @param uuid
   * @return
   */
  def validP(uuid: String): Boolean =
    try uuid != null && UUID.fromString(uuid).toString.equals(uuid)
    catch {
      case e: IllegalArgumentException => false
    }
}
