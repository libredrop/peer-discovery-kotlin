package lt.libredrop.peerdiscovery.network

import kotlinx.coroutines.flow.Flow
import kotlinx.io.core.ByteReadPacket
import lt.libredrop.peerdiscovery.data.Peer

expect class NetworkDriver constructor() {
    fun getAddresses(): List<Address>

    fun getFreePort(): Int

    fun broadcast(peer: Peer, port: Short)

    /**
     * Listens for all peers.
     * Connection will be closed after flow is consumed.
     */
    fun listenForPeers(port: Short): Flow<Peer>
}
