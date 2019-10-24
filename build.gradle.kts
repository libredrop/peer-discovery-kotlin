import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("multiplatform") version "1.3.50"
}

object Versions {
    const val coroutines = "1.3.2"
    const val snakeyaml = "1.25"
    const val junit5Version = "5.5.2"
    const val mockito = "3.1.0"
    const val mockitoKotlin = "2.2.0"
    const val kupiter = "1.0.0"
    const val kotlinxIo = "0.1.15"
    const val awaitality = "4.0.1"
}

group = "lt.libredrop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven("https://kotlin.bintray.com/kotlinx")
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-io:${Versions.kotlinxIo}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:${Versions.kotlinxIo}")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.yaml:snakeyaml:${Versions.snakeyaml}")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit5Version}")
                implementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit5Version}")
                implementation("org.junit.jupiter:junit-jupiter-params:${Versions.junit5Version}")
                implementation("lt.neworld:kupiter:${Versions.kupiter}")
                implementation("org.mockito:mockito-core:${Versions.mockito}")
                implementation("org.mockito:mockito-inline:${Versions.mockito}")
                implementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}")
                implementation("org.awaitility:awaitility-kotlin:${Versions.awaitality}")
            }
        }
    }
}

tasks {
    "jvmTest"(Test::class) {
        useJUnitPlatform()
        reports {
            html.isEnabled = true
        }

        testLogging {
            events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
    "build"(Task::class) {
        dependsOn("example:build")
    }
}

