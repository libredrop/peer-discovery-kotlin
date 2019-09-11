package lt.libredrop.peersdiscovery.data

import kotlinx.io.core.BytePacketBuilder

class MetaInfoBuilder {
    fun putInt(key: String, value: Int): MetaInfoBuilder {
        TODO()
    }

    fun build(): MetaInfo {
        val builder = BytePacketBuilder()

        //number of items
        builder.writeByte(0)

        return MetaInfo(builder.build())
    }
}
