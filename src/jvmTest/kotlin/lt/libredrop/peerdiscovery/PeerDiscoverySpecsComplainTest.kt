package lt.libredrop.peerdiscovery

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.readBytes
import lt.libredrop.peerdiscovery.network.NetworkDriver
import lt.libredrop.peerdiscovery.test.TestData
import lt.libredrop.peerdiscovery.test.assertEqualsBytes
import lt.libredrop.peerdiscovery.test.stubWith
import lt.neworld.kupiter.testFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.concurrent.CancellationException
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

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
                }

                val port: Short = 5530
                val fixture = PeerDiscovery(networkDriver, port)

                runBlockingTest {
                    val data: TestData = yaml.loadAs(file.inputStream(), TestData::class.java)

                    networkDriver.stubWith(data)

                    fixture.start(data.serviceName, data.getUUID(), data.getMetaInfo(), data.getProtocolEnum())

                    assertEquals(emptyList<Throwable>(), uncaughtExceptions)

                    val captor = argumentCaptor<ByteReadPacket>()
                    verify(networkDriver).broadcast(captor.capture(), eq(port))
                    assertEqualsBytes(data.getResultBinary().readBytes(), captor.firstValue.readBytes())
                }
            }
        }
    }
}
