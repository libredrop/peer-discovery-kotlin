package lt.libredrop.peerdiscovery.example

import kotlinx.cli.CommandLineInterface
import kotlinx.cli.parse
import kotlinx.cli.positionalArgument
import lt.libredrop.peerdiscovery.data.MetaInfoBuilder

fun main(args: Array<String>) {
    MetaInfoBuilder().build()

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
}

expect fun blocking(body: suspend () -> Unit)
