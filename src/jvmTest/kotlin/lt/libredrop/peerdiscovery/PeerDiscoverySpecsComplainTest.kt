package lt.libredrop.peerdiscovery

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.io.core.readBytes
import lt.libredrop.peerdiscovery.data.Peer
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.test.TestData
import lt.libredrop.peerdiscovery.test.assertEqualsBytes
import lt.libredrop.peerdiscovery.test.stubWith
import lt.neworld.kupiter.testFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory
import org.yaml.snakeyaml.Yaml
import java.io.File

class PeerDiscoverySpecsComplainTest {
    val yaml = Yaml()

    val testDataDir = File("specs/testData")

    @TestFactory
    fun send() = testFactory {
        testDataDir.listFiles { file: File -> file.extension == "yml" }.forEach { file ->
            test(file.nameWithoutExtension) {
                val networkDriver: NetworkDriver = mock {
                    on { getAddresses() } doReturn emptyList()
                    onGeneric { getFreePort() } doReturn 0
                    on { listenForPeers(any()) } doReturn emptyFlow()
                }

                val port: Short = 5530
                val fixture = PeerDiscovery(networkDriver, port)

                runBlockingTest {
                    val data: TestData = yaml.loadAs(file.inputStream(), TestData::class.java)

                    networkDriver.stubWith(data)

                    fixture.start(data.serviceName, data.getUUID(), data.getMetaInfo(), data.getProtocolEnum())

                    assertEquals(emptyList<Throwable>(), uncaughtExceptions)

                    val captor = argumentCaptor<Peer>()
                    verify(networkDriver).broadcast(captor.capture(), eq(port))
                    assertEqualsBytes(
                        data.getResultBinary().readBytes(),
                        captor.firstValue.createBinaryMessage().readBytes()
                    )
                }
            }
        }
    }
}
