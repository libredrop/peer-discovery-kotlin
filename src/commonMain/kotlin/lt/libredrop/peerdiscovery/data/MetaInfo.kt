package lt.libredrop.peerdiscovery.data

import kotlinx.io.core.ByteReadPacket

class MetaInfo(internal val data: ByteReadPacket) {
    fun getInt(key: String): Int {
        TODO()
    }

    companion object {
        val EMPTY: MetaInfo get() = MetaInfoBuilder().build()
    }
}
