package lt.libredrop.peerdiscovery

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.io.core.readBytes
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.test.TestData
import lt.libredrop.peerdiscovery.test.assertEqualsBytes
import lt.libredrop.peerdiscovery.test.stubWith
import lt.neworld.kupiter.testFactory
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.concurrent.TimeUnit

class PeerDiscoverySpecsComplainTest {
    val yaml = Yaml()

    val testDataDir = File("specs/testData/v01")

    @TestFactory
    fun send() = testFactory {
        testDataDir.listFiles { file: File -> file.extension == "yml" }.forEach { file ->
            test(file.nameWithoutExtension) {
                val networkDriver: NetworkDriver = mock {
                    on { getAddresses() } doReturn emptyList()
                    onGeneric { getFreePort() } doReturn 0
                    on { listenForPeers(any()) } doReturn emptyFlow()
                }

                val port = 5330
                val fixture = PeerDiscovery.Builder()
                    .networkDriver(networkDriver)
                    .port(port)
                    .build()

                runBlockingTest {
                    val data: TestData = yaml.loadAs(file.inputStream(), TestData::class.java)

                    networkDriver.stubWith(data)

                    val job = launch {
                        fixture.start(data.serviceName, data.getUUID(), data.getMetaInfo(), data.getProtocolEnum())
                    }

                    val captor = argumentCaptor<Peer>()

                    Awaitility.with().atMost(1, TimeUnit.SECONDS).untilAsserted {
                        runBlocking {
                            verify(networkDriver).broadcast(captor.capture(), eq(port))
                        }
                    }

                    assertEqualsBytes(
                        data.getResultBinary().readBytes(),
                        captor.firstValue.createBinaryMessage().readBytes()
                    )

                    assertEquals(emptyList<Throwable>(), uncaughtExceptions)

                    job.cancel()
                }
            }
        }
    }
}
