package lt.libredrop.peerdiscovery

import kotlinx.coroutines.flow.first
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class NetworkDriverTest {
    val fixture = NetworkDriver()

    val port = 5530

    @Test
    fun freePort_mustBeAbove1024() {
        val port = fixture.getFreePort()

        assertTrue(port > 1024, "Expected port to be >1024, but actually was $port")
        assertTrue(port <= 65536, "Expected port to be <= 65536, but actually was $port")
    }

    @Test
    fun listen_noErrors() = runTest {
        fixture.listenForPeers(5530).first()
    }

    @Test
    fun name() {

    }
}
