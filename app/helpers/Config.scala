package helpers

/**
 * Some application-level configurations to be loaded during startup.
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 *
 */
object Config {
  private val _pkGenerator = UUIDPrimaryKeyGenerator
  def pkGenerator = _pkGenerator
}
