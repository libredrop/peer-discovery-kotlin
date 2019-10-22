package lt.libredrop.peerdiscovery.example

import kotlinx.coroutines.runBlocking

actual fun blocking(body: suspend () -> Unit) {
    runBlocking {
        body()
    }
}
