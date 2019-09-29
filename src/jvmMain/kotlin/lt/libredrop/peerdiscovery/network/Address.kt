package lt.libredrop.peerdiscovery.network

import java.net.Inet4Address

actual typealias Address = Inet4Address

actual fun ByteArray.toAddress(): Address {
    return Inet4Address.getByAddress(this) as Inet4Address
}
