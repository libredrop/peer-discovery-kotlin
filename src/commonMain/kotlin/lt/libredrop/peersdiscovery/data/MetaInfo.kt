package lt.libredrop.peersdiscovery.data

import kotlinx.io.core.ByteReadPacket

class MetaInfo(internal val data: ByteReadPacket) {
    fun getInt(key: String): Int {
        TODO()
    }
}
