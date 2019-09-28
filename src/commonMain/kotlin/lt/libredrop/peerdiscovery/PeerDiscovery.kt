package lt.libredrop.peerdiscovery

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.io.core.BytePacketBuilder
import kotlinx.io.core.writeFully
import kotlinx.io.core.writeUByte
import lt.libredrop.peerdiscovery.data.MetaInfo
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.*

class PeerDiscovery(val networkDriver: NetworkDriver) {
    suspend fun start(
        serviceName: String,
        uuid: UUID = randomUUID(),
        metainfo: MetaInfo = MetaInfo.EMPTY,
        transportProtocol: TransportProtocol = TransportProtocol.TCP
    ): ReceiveChannel<Peer> {
        val builder = BytePacketBuilder()

        //version
        builder.writeByte(1)

        //UUID
        builder.writeFully(uuid.toByteArray())

        //service name
        builder.writeByte(serviceName.length.toByte())
        builder.writeStringUtf8(serviceName)

        //Transport protocol
        builder.writeUByte(transportProtocol.byteName)

        //port
        builder.writeShort(networkDriver.getPort())

        //ip
        val ipList = networkDriver.getAddresses()
        builder.writeByte(ipList.size.toByte())
        for (addr in ipList) {
            builder.writeFully(addr.getAddress(), 0, 4)
        }

        //metainfo
        builder.writePacket(metainfo.data)

        networkDriver.broadcast(builder.build())

        val channel = Channel<Peer>()

        return channel
    }
}
