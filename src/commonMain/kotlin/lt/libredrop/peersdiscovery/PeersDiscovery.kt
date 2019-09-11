package lt.libredrop.peersdiscovery

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.io.core.BytePacketBuilder
import lt.libredrop.peersdiscovery.data.MetaInfo
import lt.libredrop.peersdiscovery.data.Peer
import lt.libredrop.peersdiscovery.network.NetworkDriver

class PeersDiscovery(val networkDriver: NetworkDriver) {
    suspend fun start(serviceName: String, metainfo: MetaInfo): ReceiveChannel<Peer> {
        val builder = BytePacketBuilder()

        //version
        builder.writeByte(1)

//        //port
        builder.writeShort(networkDriver.getPort())
//
//        //ip
        val ipList = networkDriver.getAddresses()
        builder.writeByte(ipList.size.toByte())

//        //service name
        builder.writeByte(serviceName.length.toByte())
        builder.writeStringUtf8(serviceName)

        //metainfo
        builder.writePacket(metainfo.data)

        networkDriver.broadcast(builder.build())

        val channel = Channel<Peer>()

        return channel
    }
}
