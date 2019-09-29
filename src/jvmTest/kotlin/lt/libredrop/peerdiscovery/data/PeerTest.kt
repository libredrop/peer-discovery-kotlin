package lt.libredrop.peerdiscovery.data

import kotlinx.io.core.readBytes
import lt.libredrop.peerdiscovery.test.TestData
import lt.libredrop.peerdiscovery.test.assertEqualsBytes
import lt.neworld.kupiter.testFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory
import org.yaml.snakeyaml.Yaml
import java.io.File

class PeerTest {
    val yaml = Yaml()

    val testDataDir = File("specs/testData")

    @TestFactory
    fun serialize() = testFactory {
        testDataDir.listFiles { file: File -> file.extension == "yml" }.forEach { file ->
            test(file.nameWithoutExtension) {
                val data: TestData = yaml.loadAs(file.inputStream(), TestData::class.java)

                val peer = data.getPeer()

                assertEqualsBytes(data.getResultBinary().readBytes(), peer.createBinaryMessage().readBytes())
            }
        }
    }

    @TestFactory
    fun deserialize() = testFactory {
        testDataDir.listFiles { file: File -> file.extension == "yml" }.forEach { file ->
            container(file.nameWithoutExtension) {
                val data: TestData = yaml.loadAs(file.inputStream(), TestData::class.java)

                val peer = Peer.fromBinary(data.getResultBinary())

                test("peer info") {
                    assertEquals(data.getPeer(), peer)
                }

                for ((key, value) in data.meta) {
                    test("meta: $key") {
                        val metaInfo = peer.metaInfo
                        when (value) {
                            is String -> assertEquals(value, metaInfo.getString(key))
                            is Int -> assertEquals(value, metaInfo.getInt(key))
                            is Boolean -> assertEquals(value, metaInfo.getBoolean(key))
                            is ByteArray -> assertEqualsBytes(value, metaInfo.getByteArray(key))
                            else -> throw IllegalArgumentException("${value.javaClass} is not supported")
                        }
                    }
                }
            }
        }
    }
}
