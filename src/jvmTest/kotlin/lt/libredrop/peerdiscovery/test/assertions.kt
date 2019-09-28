package lt.libredrop.peerdiscovery.test

import org.junit.jupiter.api.Assertions

fun assertEqualsBytes(expected: ByteArray, actual: ByteArray) {
    Assertions.assertEquals(expected.toHexText(), actual.toHexText())
}

fun ByteArray.toHexText(): String {
    return map { it.toUByte().toString(16).padStart(2, '0') }
        .windowed(2, 2, true) { it.joinToString("") }
        .joinToString(" ")
}
