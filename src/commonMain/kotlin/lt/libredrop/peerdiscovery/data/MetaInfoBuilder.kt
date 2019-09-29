package lt.libredrop.peerdiscovery.data

import kotlinx.io.core.*

/**
 * Not thread-safe
 */
class MetaInfoBuilder {
    private val data = mutableListOf<Pair<String, DataTypeWriter>>()

    private fun addData(key: String, rawValue: Any) = apply {
        val keyPacket = buildPacket { writeStringUtf8(key) }
        check(keyPacket.remaining < 256) { throw IllegalArgumentException("Key length must be less than 256") }
        keyPacket.release()

        val value = when (rawValue) {
            is String -> DataTypeWriter.ForString(rawValue)
            is Int -> DataTypeWriter.ForInt(rawValue)
            is Boolean -> DataTypeWriter.ForBoolean(rawValue)
            is ByteArray -> DataTypeWriter.ForByteArray(rawValue)
            else -> throw IllegalStateException("Value type of $rawValue is not supported")
        }

        val size = value.size()
        check(size < MAX_SIZE) { "Max size of value is $MAX_SIZE bytes, but actually it has $size bytes" }

        data += key to value
    }

    fun putInt(key: String, value: Int) = addData(key, value)

    fun putString(key: String, value: String) = addData(key, value)

    fun putBoolean(key: String, value: Boolean) = addData(key, value)

    fun putByteArray(key: String, value: ByteArray) = addData(key, value)

    fun build(): MetaInfo {
        val packet = buildPacket {
            //number of items
            writeUByte(data.size.toUByte())

            val keys = BytePacketBuilder()
            val values = BytePacketBuilder()

            try {
                for ((key, value) in data) {
                    val keyPacket = buildPacket { writeStringUtf8(key) }
                    keys.writeUByte(key.length.toUByte())
                    keys.writePacket(keyPacket)

                    values.writeUShort(value.size().toUShort())
                    values.writePacket(value.getBytes())
                }
            } catch (e: Throwable) {
                keys.release()
                values.release()
                throw e
            }

            writePacket(keys.build())
            writePacket(values.build())
        }

        return MetaInfo(packet.readBytes())
    }

    companion object {
        private val MAX_SIZE = UShort.MAX_VALUE.toLong()
    }

    private sealed class DataTypeWriter {
        abstract fun size(): Long
        abstract fun getBytes(): ByteReadPacket

        class ForString(val value: String) : DataTypeWriter() {
            override fun size(): Long {
                val bytes = getBytes()
                val size = bytes.remaining
                bytes.release()
                return size
            }

            override fun getBytes() = buildPacket { writeStringUtf8(value) }
        }

        class ForBoolean(val value: Boolean) : DataTypeWriter() {
            override fun size() = 1L

            override fun getBytes() = buildPacket { writeByte(if (value) 1 else 0) }
        }

        class ForInt(val value: Int) : DataTypeWriter() {
            override fun size() = 4L

            override fun getBytes() = buildPacket { writeInt(value) }
        }

        class ForByteArray(val value: ByteArray) : DataTypeWriter() {
            override fun size() = value.size.toLong()

            override fun getBytes() = buildPacket { writeFully(value) }
        }
    }
}
