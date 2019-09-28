package lt.libredrop.peerdiscovery

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.readBytes
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.test.TestData
import lt.libredrop.peerdiscovery.test.assertEqualsBytes
import lt.libredrop.peerdiscovery.test.stubWith
import lt.neworld.kupiter.testFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory
import org.yaml.snakeyaml.Yaml
import java.io.File

class SpecsComplaintTest {
    val yaml = Yaml()

    val testDataDir = File("specs/testData")

    @TestFactory
    fun send() = testFactory {
        testDataDir.listFiles { file: File -> file.extension == "yml" }.forEach { file ->
            test(file.nameWithoutExtension) {
                val networkDriver: NetworkDriver = mock {
                    on { getAddresses() } doReturn emptyList()
                    onGeneric { getPort() } doReturn 0
                }

                val fixture = PeerDiscovery(networkDriver)

                runBlockingTest {
                    val data: TestData = yaml.loadAs(file.inputStream(), TestData::class.java)

                    networkDriver.stubWith(data)

                    fixture.start(data.serviceName, data.getUUID(), data.getMetaInfo(), data.getProtocolEnum())

                    assertEquals(emptyList<Throwable>(), uncaughtExceptions)

                    val captor = argumentCaptor<ByteReadPacket>()
                    verify(networkDriver).broadcast(captor.capture())
                    assertEqualsBytes(data.getResultBinary(), captor.firstValue.readBytes())
                }
            }
        }
    }
}
