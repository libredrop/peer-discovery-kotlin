package lt.libredrop.peerdiscovery

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.network.randomUUID
import lt.libredrop.peerdiscovery.test.runTest
import org.awaitility.Awaitility
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class PeerDiscoveryTest {
    val networkDriver = spy(NetworkDriver())

    val serviceName = "test"
    val uuid = randomUUID()
    val port = 5330

    val fixture = PeerDiscovery.Builder()
        .networkDriver(networkDriver)
        .interval(100)
        .port(port)
        .build()

    @Test
    fun start_doNotHearSelf() = runTest {
        withTimeoutOrNull(1000) {
            val peer = fixture.start(serviceName, uuid).filter { it.uuid == uuid }.first()
            throw AssertionError("Service heart self: $peer")
        }
    }

    @Test
    fun start_shoutSentFewTimes() = runTest {
        val job = launch(Dispatchers.IO) {
            fixture.start(serviceName, uuid).filter { it.uuid == uuid }.toList()
        }

        Awaitility.with().atMost(1, TimeUnit.SECONDS).untilAsserted {
            runBlocking {
                verify(networkDriver, atLeast(3)).broadcast(any(), any())
            }
        }

        job.cancel()
    }

    @Test
    fun stop_noMoreShouts() = runTest {
        val job = launch(Dispatchers.IO) {
            fixture.start(serviceName, uuid).filter { it.uuid == uuid }.toList()
        }

        delay(100)
        job.cancelAndJoin()
        clearInvocations(networkDriver)

        //Give some time to send message
        delay(200)

        verify(networkDriver, never()).broadcast(any(), any())
    }

    @Test
    fun peerFromSameServiceIsShouting_callbackIsExpected() = runTest {
        val future = async(Dispatchers.IO) {
            fixture.start(serviceName, uuid).take(1).toList()
        }

        delay(100)

        val peerFromAnotherService = Peer(serviceName = serviceName)
        networkDriver.broadcast(peerFromAnotherService, port)

        delay(100)

        val heard = withTimeout(100) {
            future.await().single()
        }

        assertEquals(peerFromAnotherService.uuid, heard.uuid)
    }

    @Test
    fun peerFromAnotherServiceIsShouting_doNotCallback() = runTest {
        val job = launch(Dispatchers.IO) {
            fixture.start(serviceName, uuid).collect {
                throw AssertionError("No callback is expected, but got: $it")
            }
        }

        delay(100)

        networkDriver.broadcast(Peer(serviceName = "Some not existing"), port)

        delay(1000)

        job.cancel()
    }

    @Test
    fun modeShout() = runTest {
        val job = launch(Dispatchers.IO) {
            fixture.start(serviceName, uuid, mode = PeerDiscovery.Mode.SHOUT).collect {
                error("This peer is not expected to collect any peer")
            }
        }

        delay(100)

        verify(networkDriver).broadcast(any(), any())

        job.cancel()
    }

    @Test
    fun modeListen() = runTest {
        val future = async(Dispatchers.IO) {
            fixture.start(serviceName, mode = PeerDiscovery.Mode.LISTEN).take(1).toList()
        }

        delay(100)

        val peer = Peer(serviceName = serviceName)
        networkDriver.broadcast(peer, port)

        withTimeout(1000) {
            assertEquals(peer, future.await().single())
        }
    }

    @Test
    fun twoPeersWithNonNormalMode_onlyListenReceivesPeer() = runTest {
        val resultOfListenPeer = async(Dispatchers.IO) {
            fixture.start(serviceName, mode = PeerDiscovery.Mode.LISTEN).take(1).toList()
        }

        delay(100)

        val job = launch(Dispatchers.IO) {
            fixture.start(serviceName, uuid, mode = PeerDiscovery.Mode.SHOUT).collect {
                error("This peer is not expected to collect any peer")
            }
        }

        withTimeout(50000) {
            val peers = resultOfListenPeer.await()

            assertEquals(uuid, peers.single().uuid)
        }

        job.cancel()
    }
}
