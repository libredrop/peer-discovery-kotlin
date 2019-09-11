package lt.libredrop.peersdiscovery

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.readBytes
import lt.libredrop.peersdiscovery.data.MetaInfoBuilder
import lt.libredrop.peersdiscovery.network.NetworkDriver
import lt.libredrop.peersdiscovery.test.TestData
import lt.neworld.kupiter.testFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.yaml.snakeyaml.Yaml
import java.io.File

class PeerDiscoveryTest {
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

                val fixture = PeersDiscovery(networkDriver)

                runBlockingTest {
                    val data: TestData = yaml.loadAs(file.inputStream(), TestData::class.java)

                    whenever(networkDriver.getPort()).thenReturn(data.port)

                    val metaInfo = MetaInfoBuilder()

                    fixture.start(data.serviceName, metaInfo.build())

                    assertEquals(emptyList<Throwable>(), uncaughtExceptions)

                    val captor = argumentCaptor<ByteReadPacket>()
                    verify(networkDriver).broadcast(captor.capture())
                    assertEqualsBytes(data.result, captor.firstValue.readBytes())
                }
            }
        }
    }

    private fun assertEqualsBytes(expected: ByteArray, actual: ByteArray) {
        assertEquals(expected.toHexText(), actual.toHexText())
    }

    private fun ByteArray.toHexText(): String {
        return map { it.toUByte().toString(16).padStart(2, '0') }
            .windowed(2, 2, true) { it.joinToString("") }
            .joinToString(" ")
    }
}
