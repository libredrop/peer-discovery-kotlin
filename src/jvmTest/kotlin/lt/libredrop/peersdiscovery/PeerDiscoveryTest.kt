package lt.libredrop.peersdiscovery

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.readBytes
import lt.libredrop.peersdiscovery.data.MetaInfo
import lt.libredrop.peersdiscovery.data.MetaInfoBuilder
import lt.libredrop.peersdiscovery.network.NetworkDriver
import lt.libredrop.peersdiscovery.test.TestData
import lt.neworld.kupiter.testFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.Inet4Address

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
                    val addresses = data.ip.map { Inet4Address.getByName(it) as Inet4Address }
                    whenever(networkDriver.getAddresses()).thenReturn(addresses)

                    fixture.start(data.serviceName, data.getMetaInfo())

                    assertEquals(emptyList<Throwable>(), uncaughtExceptions)

                    val captor = argumentCaptor<ByteReadPacket>()
                    verify(networkDriver).broadcast(captor.capture())
                    assertEqualsBytes(data.result, captor.firstValue.readBytes())
                }
            }
        }
    }

    fun TestData.getMetaInfo(): MetaInfo {
        val builder = MetaInfoBuilder()

        for ((key, value) in meta) {
            when (value) {
                is String -> builder.putString(key, value)
                is Int -> builder.putInt(key, value)
                is Boolean -> builder.putBoolean(key, value)
                is ByteArray -> builder.putByteArray(key, value)
                else -> throw IllegalArgumentException("${value.javaClass} is not supported")
            }
        }

        return builder.build()
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
