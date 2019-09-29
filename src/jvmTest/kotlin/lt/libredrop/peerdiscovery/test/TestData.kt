package lt.libredrop.peerdiscovery.test

import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.buildPacket
import lt.libredrop.peerdiscovery.data.MetaInfo
import lt.libredrop.peerdiscovery.data.MetaInfoBuilder
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.TransportProtocol
import java.net.Inet4Address
import java.util.*

class TestData {
    var serviceName: String = ""
    var port: Short = 0
    var result: String = ""
    var ip: List<String> = emptyList()
    var meta: Map<String, Any> = emptyMap()
    var protocol: String = "tcp"
    var uuid: String = "00000000-0000-0000-0000-000000000000"

    fun getMetaInfo(): MetaInfo {
        val builder = MetaInfoBuilder()

        for ((key, value) in meta) {
            when (value) {
                is String -> builder.putString(key, value)
                is Int -> builder.putInt(key, value)
                is Boolean -> builder.putBoolean(key, value)
                is ByteArray -> builder.putByteArray(key, value)
                else -> throw IllegalArgumentException("${value.javaClass} is not supported")
            }
        }

        return builder.build()
    }

    fun getPeer(): Peer {
        return Peer(
            addresses = ip.map { Inet4Address.getByName(it) as Inet4Address },
            port = port.toUShort(),
            uuid = getUUID(),
            serviceName = serviceName,
            transportProtocol = getProtocolEnum(),
            metaInfo = getMetaInfo()
        )
    }

    fun getResultBinary(): ByteReadPacket {
        val bytes = result.lineSequence()
            .map { it.takeWhile { it != '#' } }
            .joinToString()
            .filter { it.isLetterOrDigit() }
            .chunked(2) { it.toString().toUByte(16) }
            .toUByteArray()
            .toByteArray()

        return buildPacket {
            writeFully(bytes, 0, bytes.size)
        }
    }

    fun getUUID(): UUID = UUID.fromString(uuid)

    fun getProtocolEnum(): TransportProtocol {
        return when (protocol.toLowerCase()) {
            "tcp" -> TransportProtocol.TCP
            "udp" -> TransportProtocol.UDP
            else -> throw IllegalStateException("protocol name '$protocol' is not supported")
        }
    }
}
