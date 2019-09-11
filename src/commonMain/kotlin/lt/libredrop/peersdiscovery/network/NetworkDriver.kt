package lt.libredrop.peersdiscovery.network

import kotlinx.io.core.BytePacketBuilder
import kotlinx.io.core.ByteReadPacket

interface NetworkDriver {
    fun getAddresses(): List<Address>

    fun getPort(): Short

    fun broadcast(message: ByteReadPacket)
}
