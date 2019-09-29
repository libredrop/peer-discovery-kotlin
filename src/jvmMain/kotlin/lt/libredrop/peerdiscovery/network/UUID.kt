package lt.libredrop.peerdiscovery.network

import java.nio.ByteBuffer
import java.util.UUID

actual typealias UUID = java.util.UUID

actual fun UUID.toByteArray(): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)

    return bb.array()
}

actual fun ByteArray.toUUID(): UUID {
    val bb = ByteBuffer.wrap(this)
    val high = bb.long
    val low = bb.long
    return UUID(high, low)
}

actual fun randomUUID(): UUID = UUID.randomUUID()
