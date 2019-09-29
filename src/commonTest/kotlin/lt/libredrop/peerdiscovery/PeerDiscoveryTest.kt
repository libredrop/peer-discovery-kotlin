package lt.libredrop.peerdiscovery

import kotlinx.coroutines.flow.first
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.test.runTest
import kotlin.test.Test

class PeerDiscoveryTest {
    val networkDriver = NetworkDriver()

    val fixture = PeerDiscovery(networkDriver)

    @Test
    fun listen_noErrors() = runTest {
        fixture.listen().first()
    }
}
