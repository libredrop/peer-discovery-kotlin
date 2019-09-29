package lt.libredrop.peerdiscovery.network

expect class Address {
    fun getAddress(): ByteArray
}

expect fun ByteArray.toAddress(): Address
