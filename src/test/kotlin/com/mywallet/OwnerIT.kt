package com.mywallet

import com.mywallet.application.owner.usecases.CreateOwnerUseCase
import com.mywallet.application.owner.usecases.QueryOwnerUseCase
import com.mywallet.infrastructure.owner.gateways.CreateOwnerRepository
import com.mywallet.infrastructure.owner.gateways.QueryOwnerRepository
import com.mywallet.infrastructure.owner.gateways.ValidateOwnerGateway
import com.mywallet.infrastructure.owner.graphql.OwnerMutation
import com.mywallet.infrastructure.owner.graphql.OwnerQuery
import com.mywallet.plugins.MyWalletIntegrationConfig
import com.mywallet.plugins.Neo4jConnection
import com.mywallet.plugins.asJsonPrimitive
import com.mywallet.plugins.isValidUUID
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OwnerIT : MyWalletIntegrationConfig() {

    private fun Application.setupModule() = myWalletModule {
        packages = listOf("com.mywallet")
        queries = listOf(
            OwnerQuery(
                queryOwnerUseCase = QueryOwnerUseCase(
                    queryOwnerGateway = QueryOwnerRepository(Neo4jConnection(connectionConfig))
                )
            )
        )
        mutations = listOf(
            OwnerMutation(
                useCase = CreateOwnerUseCase(
                    createOwnerRepositoryGateway = CreateOwnerRepository(Neo4jConnection(connectionConfig)),
                    validateOwnerGateway = ValidateOwnerGateway()
                )
            )
        )
    }


    @Test
    fun `it should create a owner`() = testApplication {
        application { setupModule() }
        val createOwner = """
               mutation Owner {
                    createOwner(input: { name: "Luis Camilo" }) { name, publicId }
                }
            """.asJsonPrimitive()

        val bodyRequest = buildJsonObject {
            put("query", createOwner)
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
        }
        val bodyAsText = response.bodyAsText()
        val output = Json.parseToJsonElement(bodyAsText)
            .jsonObject["data"]
            ?.jsonObject?.get("createOwner")



        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(output)
        assertEquals("Luis Camilo", output.jsonObject["name"]!!.jsonPrimitive.content)
        assertNotNull(output.jsonObject["publicId"]!!.jsonPrimitive.content)
        assertTrue(isValidUUID(output.jsonObject["publicId"]!!.jsonPrimitive.content), "publicId should be UUID")

    }

    @Test
    fun `it should get a owner by full name`() = testApplication {
        application { setupModule() }
        val ownerPublicId = createOwner(name = "Luis Camilo")?.jsonObject?.get("publicId")?.jsonPrimitive?.content ?: ""

        val createOwner = """
               query {
                    findOwnerBy(publicId: "$ownerPublicId") { publicId, name }
                }
            """.asJsonPrimitive()

        val bodyRequest = buildJsonObject {
            put("query", createOwner)
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
        }
        val bodyAsText = response.bodyAsText()
        val output = Json.parseToJsonElement(bodyAsText)
            .jsonObject["data"]
            ?.jsonObject!!["findOwnerBy"]



        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(output)
        assertEquals("Luis Camilo", output.jsonObject["name"]!!.jsonPrimitive.content)
        assertNotNull(output.jsonObject["publicId"]!!.jsonPrimitive.content)
        assertTrue(isValidUUID(output.jsonObject["publicId"]!!.jsonPrimitive.content), "publicId should be UUID")


    }

    private suspend fun ApplicationTestBuilder.createOwner(name: String): JsonElement? {
        val createOwner = """
                   mutation Owner {
                        createOwner(input: { name: "$name" }) { name, publicId }
                    }
                """.asJsonPrimitive()

        val bodyRequest = buildJsonObject {
            put("query", createOwner)
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
        }
        val bodyAsText = response.bodyAsText()
        return Json.parseToJsonElement(bodyAsText)
            .jsonObject["data"]
            ?.jsonObject?.get("createOwner")
    }

}