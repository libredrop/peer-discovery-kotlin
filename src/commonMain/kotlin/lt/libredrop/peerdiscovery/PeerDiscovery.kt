package lt.libredrop.peerdiscovery

import kotlinx.coroutines.flow.Flow
import lt.libredrop.peerdiscovery.data.MetaInfo
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.network.TransportProtocol
import lt.libredrop.peerdiscovery.network.UUID
import lt.libredrop.peerdiscovery.network.randomUUID

class PeerDiscovery(private val networkDriver: NetworkDriver, private val port: Int = 5530) {
    suspend fun start(
        serviceName: String,
        uuid: UUID = randomUUID(),
        metainfo: MetaInfo = MetaInfo.EMPTY,
        transportProtocol: TransportProtocol = TransportProtocol.TCP
    ): Flow<Peer> {
        broadcast(uuid, serviceName, transportProtocol, metainfo)

        return listen()
    }

    internal fun listen(): Flow<Peer> {
        return networkDriver.listenForPeers(port)
    }

    internal suspend fun broadcast(
        uuid: UUID,
        serviceName: String,
        transportProtocol: TransportProtocol,
        metainfo: MetaInfo
    ) {
        val peer = Peer(
            addresses = networkDriver.getAddresses(),
            port = networkDriver.getFreePort().toUShort(),
            uuid = uuid,
            serviceName = serviceName,
            transportProtocol = transportProtocol,
            metaInfo = metainfo
        )

        networkDriver.broadcast(peer, port)
    }
}
