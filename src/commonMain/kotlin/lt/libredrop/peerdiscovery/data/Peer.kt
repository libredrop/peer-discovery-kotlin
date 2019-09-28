package lt.libredrop.peerdiscovery.data

import lt.libredrop.peerdiscovery.network.Address

data class Peer(
    val addresses: List<Address>,
    val port: UInt,
    val metaInfo: MetaInfo
)
