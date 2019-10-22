package lt.libredrop.peerdiscovery.example

import kotlinx.cli.CommandLineInterface
import kotlinx.cli.parse
import kotlinx.cli.positionalArgument
import lt.libredrop.peerdiscovery.PeerDiscovery

fun main(args: Array<String>) {
    val cli = CommandLineInterface("kotlin MainKt")

    val serviceName by cli.positionalArgument(
        name = "SERVICE_NAME",
        help = "Service name used to recognize peers of the same service. All parties must use same name",
        initialValue = "test"
    )

    try {
        cli.parse(args)
    } catch (e: Exception) {
        return
    }

    val peerDiscovery = PeerDiscovery.Builder()
        .build()

    blocking {
        peerDiscovery.start(serviceName)
    }
}

expect fun blocking(body: suspend () -> Unit)
