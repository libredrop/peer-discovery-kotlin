package lt.libredrop.peerdiscovery.data

import kotlinx.io.core.*
import lt.libredrop.peerdiscovery.network.*

data class Peer(
    val addresses: List<Address> = emptyList(),
    val port: UShort = 0u,
    val uuid: UUID = randomUUID(),
    val serviceName: String = "",
    val transportProtocol: TransportProtocol = TransportProtocol.TCP,
    val metaInfo: MetaInfo = MetaInfo.EMPTY
) {
    fun createBinaryMessage(): ByteReadPacket {
        val builder = BytePacketBuilder()

        //version
        builder.writeByte(1)

        //UUID
        builder.writeFully(uuid.toByteArray())

        //service name
        builder.writeUByte(serviceName.length.toUByte())
        builder.writeStringUtf8(serviceName)

        //Transport protocol
        builder.writeUByte(transportProtocol.byteName)

        //port
        builder.writeUShort(port)

        //ip
        builder.writeUByte(addresses.size.toUByte())
        for (addr in addresses) {
            builder.writeFully(addr.getAddress())
        }

        //metainfo
        builder.writeFully(metaInfo.data)

        return builder.build()
    }

    companion object {
        fun fromBinary(data: ByteReadPacket): Peer {
            val version = data.readByte()

            val uuid = data.readBytes(16).toUUID()

            val serviceNameLength = data.readUByte()
            val servieName = data.readTextExactBytes(serviceNameLength.toInt())

            val transportProtocol = data.readUByte().let { byte ->
                TransportProtocol.values().find { it.byteName == byte }!!
            }

            val port = data.readUShort()

            val ipAddressNum = data.readUByte()
            val addresses = sequence {
                repeat(ipAddressNum.toInt()) {
                    yield(data.readBytes(4).toAddress())
                }
            }.toList()

            val metaInfoBinary = data.readBytes()

            return Peer(
                addresses = addresses,
                port = port,
                uuid = uuid,
                serviceName = servieName,
                transportProtocol = transportProtocol,
                metaInfo = MetaInfo(metaInfoBinary)
            )
        }
    }
}
