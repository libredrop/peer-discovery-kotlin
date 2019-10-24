plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/orangy/maven")
    maven("https://jitpack.io")
}

object Versions {
    const val coroutines = "1.3.2"
    const val cli = "0.1.0-dev-5"
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
                implementation(rootProject)
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-cli-metadata:${Versions.cli}") {
                    isTransitive = false
                }
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:${Versions.cli}")
            }
        }
    }
}

tasks.create("run", JavaExec::class) {
    dependsOn("jvmMainClasses", "jvmJar")
    main = "lt.libredrop.peerdiscovery.example.MainKt"
    classpath = files(
        kotlin.targets["jvm"].compilations["main"].output.allOutputs,
        configurations["jvmRuntimeClasspath"]
    )
}
