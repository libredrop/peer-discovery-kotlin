package lt.libredrop.peerdiscovery.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import lt.libredrop.peerdiscovery.data.Peer
import java.net.ServerSocket



actual class NetworkDriver {
    actual fun getAddresses(): List<Address> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getFreePort(): Int {
        val s = ServerSocket(0)
        return s.localPort
    }

    actual fun broadcast(peer: Peer, port: Short) {
        TODO()
    }

    /**
     * Listens for all peers.
     * Connection will be closed after flow is consumed.
     */
    actual fun listenForPeers(port: Short): Flow<Peer> {
        return flowOf(Peer())
    }
}
