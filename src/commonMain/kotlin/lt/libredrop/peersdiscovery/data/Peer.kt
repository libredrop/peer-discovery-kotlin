package lt.libredrop.peersdiscovery.data

import lt.libredrop.peersdiscovery.network.Address

data class Peer(
    val addresses: List<Address>,
    val port: UInt,
    val metaInfo: MetaInfo
)
