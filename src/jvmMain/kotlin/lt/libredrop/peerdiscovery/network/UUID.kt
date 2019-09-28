package lt.libredrop.peerdiscovery.network

import java.nio.ByteBuffer

actual typealias UUID = java.util.UUID

actual fun UUID.toByteArray(): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)

    return bb.array()
}

actual fun ByteArray.toUUID(): UUID = java.util.UUID.nameUUIDFromBytes(this)

actual fun randomUUID(): UUID = UUID.randomUUID()
