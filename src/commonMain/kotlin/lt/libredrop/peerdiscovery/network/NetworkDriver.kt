package lt.libredrop.peerdiscovery.network

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.io.core.ByteReadPacket
import lt.libredrop.peerdiscovery.data.Peer

interface NetworkDriver {
    fun getAddresses(): List<Address>

    fun getFreePort(): Short

    fun broadcast(message: ByteReadPacket, port: Short)

    /**
     * Listens for all peers.
     * You must [ReceiveChannel.cancel] after you are finished listening for peers.
     * Otherwise connection will not be closed and leaked.
     */
    fun listenForPeers(port: UShort): ReceiveChannel<Peer>
}
