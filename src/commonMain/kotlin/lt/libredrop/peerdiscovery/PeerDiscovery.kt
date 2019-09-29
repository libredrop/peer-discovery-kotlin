package lt.libredrop.peerdiscovery

import kotlinx.coroutines.channels.ReceiveChannel
import lt.libredrop.peerdiscovery.data.MetaInfo
import lt.libredrop.peerdiscovery.data.MetaInfoBuilder
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.network.TransportProtocol
import lt.libredrop.peerdiscovery.network.UUID
import lt.libredrop.peerdiscovery.network.randomUUID

class PeerDiscovery(private val networkDriver: NetworkDriver, private val port: Short = 5530) {
    suspend fun start(
        serviceName: String,
        uuid: UUID = randomUUID(),
        metainfo: MetaInfo = MetaInfo.EMPTY,
        transportProtocol: TransportProtocol = TransportProtocol.TCP
    ): ReceiveChannel<Peer> {
        val thisPeer = Peer(
            addresses = networkDriver.getAddresses(),
            port = networkDriver.getFreePort().toUShort(),
            uuid = uuid,
            serviceName = serviceName,
            transportProtocol = transportProtocol,
            metaInfo = metainfo
        )

        networkDriver.broadcast(thisPeer.createBinaryMessage(), port)

        return networkDriver.listenForPeers(port.toUShort())
    }
}
