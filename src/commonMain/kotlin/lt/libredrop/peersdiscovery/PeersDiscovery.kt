package lt.libredrop.peersdiscovery

import kotlinx.coroutines.channels.ReceiveChannel
import lt.libredrop.peersdiscovery.data.MetaInfo
import lt.libredrop.peersdiscovery.data.Peer

class PeersDiscovery(val networkDriver: NetworkDriver) {
    suspend fun start(serviceName: String, metainfo: MetaInfo): ReceiveChannel<Peer> {
        TODO()
    }
}
