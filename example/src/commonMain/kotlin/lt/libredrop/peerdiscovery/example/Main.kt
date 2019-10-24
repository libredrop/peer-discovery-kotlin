package lt.libredrop.peerdiscovery.example

import kotlinx.cli.CommandLineInterface
import kotlinx.cli.flagValueArgument
import kotlinx.cli.parse
import kotlinx.cli.positionalArgument
import kotlinx.coroutines.flow.collect
import lt.libredrop.peerdiscovery.PeerDiscovery

fun main(args: Array<String>) {
    val cli = CommandLineInterface("kotlin MainKt")

    val mode by cli.flagValueArgument<PeerDiscovery.Mode>(
        flags = listOf("--mode", "-m"),
        valueSyntax = "MODE",
        help = """
            MODE variants: NORMAL | SHOUT | LISTEN
        """.trimIndent(),
        initialValue = PeerDiscovery.Mode.NORMAL,
        mapping = { value: String -> PeerDiscovery.Mode.valueOf(value.toUpperCase()) }
    )

    val serviceName by cli.positionalArgument(
        name = "SERVICE_NAME",
        help = "Service name used to recognize peers of the same service. All parties must use same name",
        minArgs = 1
    )

    try {
        cli.parse(args)
    } catch (e: Exception) {
        return
    }

    val peerDiscovery = PeerDiscovery.Builder()
        .build()

    blocking {
        peerDiscovery.start(serviceName!!, mode = mode).collect {
            println("Discovered peer: $it")
        }
    }
}

expect fun blocking(body: suspend () -> Unit)
