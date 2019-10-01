package lt.libredrop.peerdiscovery

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.single
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
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
        withTimeoutOrNull(10) {
            fixture.listenForPeers(port).first()
        }
    }

    @Test
    fun simpleSendAndGetPeer() = runTest {
        val peer = Peer(serviceName = "test", port = 4000u)

        val futurePeer = async { fixture.listenForPeers(port).first() }

        launch {
            repeat(100) {
                delay(10)
                fixture.broadcast(peer, port)
            }
        }

        withTimeout(1000) {
            val actual = futurePeer.await()
            assertEquals(peer, actual)
        }
    }

    @Test
    fun doubleListen_crash() = runTest {
        val job = fixture.listenForPeers(port).launchIn(this)
        yield()

        assertFails {
            fixture.listenForPeers(port).single()
        }

        job.cancel()
    }

    @Test
    fun jobStop_unbind() = runTest {
        val job = fixture.listenForPeers(port).launchIn(this)
        yield()

        job.cancel()

        withTimeoutOrNull(10) {
            fixture.listenForPeers(port).single()
        }
    }
}
