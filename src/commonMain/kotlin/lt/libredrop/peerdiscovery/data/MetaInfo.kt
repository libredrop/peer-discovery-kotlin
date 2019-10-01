package lt.libredrop.peerdiscovery.data

import kotlinx.io.core.*

class MetaInfo internal constructor(internal val data: ByteArray) {
    private val map: Map<String, ByteArray> by lazy {
        val packet = buildPacket {
            writeFully(data)
        }

        packet.use {
            val count = packet.readUByte().toInt()

            val keys = sequence {
                repeat(count) {
                    val size = packet.readUByte().toInt()
                    yield(packet.readTextExactBytes(size))
                }
            }.toList()

            val values = sequence {
                repeat(count) {
                    val size = packet.readUShort().toInt()
                    yield(packet.readBytesOf(size, size))
                }
            }.toList()

            check(!packet.isNotEmpty) { "Data packet is broken. There is ${packet.remaining} bytes left" }

            keys.zip(values).toMap()
        }
    }

    fun getInt(key: String): Int {
        return ByteReadPacket(map.getValue(key)).use {
            it.readInt()
        }
    }

    fun getString(key: String): String {
        return ByteReadPacket(map.getValue(key)).use {
            it.readText()
        }
    }

    fun getBoolean(key: String): Boolean {
        return ByteReadPacket(map.getValue(key)).use {
            val zero: Byte = 0
            it.readByte() != zero
        }
    }

    fun getByteArray(key: String): ByteArray {
        return map.getValue(key)
    }

    fun validateMetaInfo(): Boolean {
        return map.size >= 0
    }

    override fun toString(): String {
        return "MetaInfo([${map.keys.joinToString(", ")}])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MetaInfo

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }

    companion object {
        val EMPTY: MetaInfo get() = MetaInfoBuilder().build()
    }
}
