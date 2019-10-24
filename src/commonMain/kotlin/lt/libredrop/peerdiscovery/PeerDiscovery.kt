package lt.libredrop.peerdiscovery

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import lt.libredrop.peerdiscovery.data.MetaInfo
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.network.TransportProtocol
import lt.libredrop.peerdiscovery.network.UUID
import lt.libredrop.peerdiscovery.network.randomUUID
import kotlin.coroutines.coroutineContext

class PeerDiscovery private constructor(
    private val networkDriver: NetworkDriver,
    private val port: Int,
    private val interval: Long
) {
    /**
     * @param serviceName name of service must be the same across peers.
     * @param uuid for custom [UUID] you must ensure it is unique.
     * @param mode if all peers are equal, you should leave it [Mode.NORMAL].
     * For slave-master configuration please check [Mode.LISTEN] and [Mode.SHOUT]
     */
    suspend fun start(
        serviceName: String,
        uuid: UUID = randomUUID(),
        metainfo: MetaInfo = MetaInfo.EMPTY,
        transportProtocol: TransportProtocol = TransportProtocol.TCP,
        mode: Mode = Mode.NORMAL
    ): Flow<Peer> {
        val mainJob = Job(coroutineContext[Job])
        val scope = CoroutineScope(mainJob)

        if (mode in listOf(Mode.NORMAL, Mode.SHOUT)) {
            scope.launch {
                while (isActive) {
                    broadcast(uuid, serviceName, transportProtocol, metainfo)
                    delay(interval)
                }
            }
        }

        val flow = if (mode in listOf(Mode.NORMAL, Mode.LISTEN)) {
            listen().filter { it.serviceName == serviceName && it.uuid != uuid }
        } else {
            flow { mainJob.join() }
        }

        return flow.onCompletion { scope.cancel() }
    }

    private fun listen(): Flow<Peer> {
        return networkDriver.listenForPeers(port)
    }

    private suspend fun broadcast(
        uuid: UUID,
        serviceName: String,
        transportProtocol: TransportProtocol,
        metainfo: MetaInfo
    ) {
        val peer = Peer(
            addresses = networkDriver.getAddresses(),
            port = networkDriver.getFreePort().toUShort(),
            uuid = uuid,
            serviceName = serviceName,
            transportProtocol = transportProtocol,
            metaInfo = metainfo
        )

        networkDriver.broadcast(peer, port)
    }

    class Builder() {
        private var networkDriver = NetworkDriver()
        private var port = 5330
        private var interval = 3000L

        /**
         * You can replace network driver with your own. It is mostly used for tests.
         */
        fun networkDriver(networkDriver: NetworkDriver) = apply { this.networkDriver = networkDriver }

        /**
         * @Param port default is 5330. All peers must use the same port for discovery.
         */
        fun port(port: Int) = apply { this.port = port }

        /**
         * @param interval is a time between shouts in milliseconds.
         * Default is recommended 3000.
         * Shorter intervals could flood network unexpectedly.
         */
        fun interval(interval: Long) = apply { this.interval = interval }

        fun build() = PeerDiscovery(
            networkDriver = networkDriver,
            port = port,
            interval = interval
        )
    }

    enum class Mode {
        /**
         * Listen and shout. This is default mode
         */
        NORMAL,

        /**
         * You can use [LISTEN] mode if you do not want expose this peer.
         * It is useful for user devices in master-slave peers configuration.
         */
        LISTEN,

        /**
         * You can use [SHOUT] mode to reveal this peer other peers.
         * It is useful for service provider in master-slave peers configuration.
         */
        SHOUT,
    }
}
