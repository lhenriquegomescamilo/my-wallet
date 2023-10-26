package com.mywallet

import com.mywallet.plugins.DatabaseConnectionConfig
import com.mywallet.plugins.Neo4jConnection
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation.Plugin
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import io.ktor.util.InternalAPI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.testcontainers.containers.Neo4jContainer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

class ApplicationTest {
    private lateinit var neo4jContainer: Neo4jContainer<Nothing>

    private lateinit var connectionConfig: DatabaseConnectionConfig

    @BeforeTest
    fun beforeSetup() {
        neo4jContainer = Neo4jContainer<Nothing>("neo4j:community-ubi8").apply {
            withAdminPassword("test_neo4j_password")
        }
        neo4jContainer.start()

        connectionConfig = object : DatabaseConnectionConfig {
            override val uri: String
                get() = neo4jContainer.boltUrl
            override val user: String
                get() = "neo4j"
            override val password: String
                get() = neo4jContainer.adminPassword

        }
    }

    @AfterTest
    fun afterTest() {
        neo4jContainer.stop()
    }

    @OptIn(InternalAPI::class)
    @Test
    fun `it should create a category`() = testApplication {


        application {
            module {
                packages = listOf("com.mywallet")
                queries = listOf(
                    WalletQuery(Neo4jConnection(connectionConfig))
                )
            }
        }
        val query = """
            query {
              findAll {
                name
              }
            }
          """

        val bodyRequest = buildJsonObject {
            put("query", JsonPrimitive(query))
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(bodyRequest)
            body = bodyRequest
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
