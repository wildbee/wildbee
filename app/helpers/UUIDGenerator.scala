package helpers

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 *
 */
object UUIDGenerator {
  // TODO: we shouldn't use SUN's base64 Encoder
  // Use apache solution instead
  val encoder = new sun.misc.BASE64Encoder();

  def UUID: String = {
    java.util.UUID.randomUUID().toString()
  }

  def compressedUUID(): String = {
    encoder.encode(asByteArray(java.util.UUID.randomUUID))
  }

  def trimmedUUID(): String = {
    UUIDGenerator.compressedUUID.split("==")(0)
  }

  def asByteArray(uuid: java.util.UUID): Array[Byte] = {
    val bytes = new Array[Byte](16)
    val buffer = java.nio.ByteBuffer.wrap(bytes);
    buffer.putLong(uuid.getMostSignificantBits)
    buffer.putLong(uuid.getLeastSignificantBits)
    buffer.array
  }
}
