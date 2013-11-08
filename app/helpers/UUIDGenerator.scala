package helpers

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 *
 */
object UUIDGenerator {
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
    val msb = uuid.getMostSignificantBits
    val lsb = uuid.getLeastSignificantBits
    val buffer = new Array[Byte](16)
    for (i <- 0 until 8) buffer(i) = (msb >>> 8 * (7 - i)).toByte
    for (i <- 8 until 16) buffer(i) = (lsb >>> 8 * (7 - i)).toByte
    buffer
  }
}
