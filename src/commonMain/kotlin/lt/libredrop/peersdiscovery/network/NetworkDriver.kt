package lt.libredrop.peersdiscovery

import lt.libredrop.peersdiscovery.network.Address

interface NetworkDriver {
    fun getAddresses(): List<Address>

    fun getPort(): UInt

    fun broadcast(message: ByteArray)
}
