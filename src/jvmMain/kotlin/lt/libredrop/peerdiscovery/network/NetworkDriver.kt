package lt.libredrop.peerdiscovery.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import lt.libredrop.peerdiscovery.data.Peer
import java.net.*

actual class NetworkDriver {
    actual fun getAddresses(): List<Address> {
        return NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { !it.isLoopback && it.isUp }
            .mapNotNull { it.inetAddresses as? Inet4Address? }
            .toList()
    }

    actual fun getFreePort(): Int {
        val s = ServerSocket(0)
        return s.localPort
    }

    actual suspend fun broadcast(peer: Peer, port: Int) {
        val body = peer.createBinaryMessage().readBytes()
        val broadcastAddr = Inet4Address.getByName("255.255.255.255")

        val packet = DatagramPacket(body, body.size, broadcastAddr, port)

        val datagramSocket = DatagramSocket()
        datagramSocket.broadcast = true
        withContext(Dispatchers.IO) {
            datagramSocket.send(packet)
        }
        datagramSocket.close()
    }

    /**
     * Listens for all peers.
     * Connection will be closed after flow is consumed.
     */
    actual fun listenForPeers(port: Int): Flow<Peer> {
        return channelFlow {

            val serverSocket = DatagramSocket(port)

            invokeOnClose {
                serverSocket.close()
            }

            val buffer = ByteArray(65536)
            try {
                while (true) {
                    val receivePacket = DatagramPacket(buffer, buffer.size)
                    withContext(Dispatchers.IO) {
                        serverSocket.receive(receivePacket)
                    }

                    val bytePacket = buildPacket {
                        writeFully(buffer, 0, receivePacket.length)
                    }

                    try {
                        val peer = Peer.fromBinary(bytePacket)
                        peer.metaInfo.validateMetaInfo()

                        check(bytePacket.remaining == 0L) { "Packet is invalid. Packet has unused ${bytePacket.release()} bytes left" }

                        send(peer)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e

                        System.err.println("Failed parse packet ${e.localizedMessage}")
                    } finally {
                        bytePacket.release()
                    }
                }
            } catch (e: SocketException) {
                //socket exception are expected if flow is terminated
            }
        }
    }
}
