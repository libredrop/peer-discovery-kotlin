package lt.libredrop.peerdiscovery.network

import kotlinx.io.core.ByteReadPacket

interface NetworkDriver {
    fun getAddresses(): List<Address>

    fun getPort(): Short

    fun broadcast(message: ByteReadPacket)
}
