package lt.libredrop.peerdiscovery.network

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.io.core.readBytes
import lt.libredrop.peerdiscovery.data.MetaInfoBuilder
import lt.libredrop.peerdiscovery.data.Peer
import org.junit.jupiter.api.Test
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address

class NetworkDriverJvmTest {
    val fixture = NetworkDriver()

    val port = 5530

    @Test
    fun invalidPackages_shouldBeSkipped() = runBlocking {
        val future = launch {
            fixture.listenForPeers(port).collect {
                throw AssertionError("All packages are broken in this test. But one package go through: $it")
            }
        }

        val addresses = listOf(
            Address.getByName("192.168.0.1"),
            Address.getByName("127.0.0.1")
        ).map { it as Inet4Address }

        val peer = Peer(
            addresses = addresses,
            serviceName = "Foo bar",
            metaInfo = MetaInfoBuilder().putString("Hello", "world").build(),
            transportProtocol = TransportProtocol.UDP,
            port = 1234U
        )

        val goodPacket = peer.createBinaryMessage().readBytes()

        for (i in 1..goodPacket.size) {
            val broadcastAddr = Inet4Address.getByName("255.255.255.255")

            val packet = DatagramPacket(goodPacket, goodPacket.size - i, broadcastAddr, port)

            val datagramSocket = DatagramSocket()
            withContext(Dispatchers.IO) {
                datagramSocket.send(packet)
            }
        }

        delay(1000)

        future.cancel()
    }
}
