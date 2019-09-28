package lt.libredrop.peerdiscovery.network

expect class UUID

expect fun UUID.toByteArray(): ByteArray

expect fun ByteArray.toUUID(): UUID

expect fun randomUUID(): UUID
