val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val expedia_graphql_version: String by project
val koin_version: String by project
val neo4j_driver_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("org.graalvm.buildtools.native") version "0.9.20"
    id("com.expediagroup.graphql") version "7.0.1"
}

group = "com.mywallet"
version = "0.0.1"

application {
    mainClass.set("com.mywallet.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.expediagroup/graphql-kotlin-server
    implementation("com.expediagroup", "graphql-kotlin-ktor-server", expedia_graphql_version)

    implementation("org.neo4j.driver", "neo4j-java-driver", neo4j_driver_version)
    implementation("org.neo4j:neo4j-cypher-dsl:2023.7.0")

    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-client-encoding:2.3.5")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")


    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // Test container
    implementation("org.testcontainers:testcontainers:1.16.3")
    implementation("org.testcontainers:neo4j:1.16.3")
}

//
graphql {
    graalVm {
        packages = listOf("com.mywallet")
    }
}

graalvmNative {
    toolchainDetection.set(false)
    binaries {
        named("main") {
            verbose.set(true)
            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin,ch.qos.logback,org.slf4j")
            buildArgs.add("-H:+ReportExceptionStackTraces")
        }
        metadataRepository {
            enabled.set(true)
        }
    }
}