package lt.libredrop.peerdiscovery.test

import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import lt.libredrop.peerdiscovery.network.NetworkDriver
import java.net.Inet4Address

fun NetworkDriver.stubWith(testData: TestData) {
    whenever(getAddresses()).thenReturn(testData.ip.map { Inet4Address.getByName(it) as Inet4Address })
    whenever(getFreePort()).thenReturn(testData.port)
}

actual fun runTest(body: suspend () -> Unit) {
    runBlocking {
        body()
    }
}
