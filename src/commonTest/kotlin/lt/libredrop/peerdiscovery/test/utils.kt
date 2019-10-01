package lt.libredrop.peerdiscovery.test

import kotlinx.coroutines.CoroutineScope

expect fun runTest(body: suspend CoroutineScope.() -> Unit)
