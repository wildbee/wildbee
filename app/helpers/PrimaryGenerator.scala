package helpers

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 *
 */
trait PrimaryKeyGenerator {
  def newKey : Any
}

object UUIDPrimaryKeyGenerator extends PrimaryKeyGenerator {
  def newKey: Any = java.util.UUID.randomUUID
}
