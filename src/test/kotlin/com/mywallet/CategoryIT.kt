package com.mywallet

import com.mywallet.application.category.usecases.CreateCategoryUseCase
import com.mywallet.infrastructure.category.gateways.CategoryRepositoryGateway
import com.mywallet.infrastructure.category.graphql.CategoryMutation
import com.mywallet.infrastructure.category.graphql.CategoryQuery
import com.mywallet.plugins.MyWalletIntegrationConfig
import com.mywallet.plugins.Neo4jConnection
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
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CategoryIT : MyWalletIntegrationConfig() {

    private fun Application.setupModule() = myWalletModule {
        packages = listOf("com.mywallet")
        queries = listOf(CategoryQuery())
        mutations = listOf(
            CategoryMutation(
                CreateCategoryUseCase(
                    CategoryRepositoryGateway(Neo4jConnection(connectionConfig))
                )
            )
        )
    }

    @Test
    fun `it should create a category`() = testApplication {
        application {
            setupModule()
        }
        val query = """
            mutation CreateCategory {
              createCategory(input: {name: "Supermercados"}){
                name,
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
        }
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(body)
        assertEquals("{\"data\":{\"createCategory\":{\"name\":\"Supermercados\"}}}", body)
    }

    @Test
    fun `it should return a error when try to duplicate a category`() = testApplication {
        application {
            setupModule()
        }
        val query = """
            mutation CreateCategory {
              createCategory(input: {name: "Supermercados"}){
                name,
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

        client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(bodyRequest)
        }

        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(bodyRequest)
        }

        val body = response.bodyAsText()
        val output = Json.parseToJsonElement(body)
            .jsonObject["errors"]!!
            .jsonArray[0]
            .jsonObject["message"]!!
            .jsonPrimitive.content

        assertNotNull(body)
        assertEquals("The category Supermercados already exists", output)
    }

    @Test
    fun `it should return a error when try to input empty name to category `() = testApplication {
        application {
            setupModule()
        }
        val query = """
            mutation CreateCategory {
              createCategory(input: {name: ""}){
                name,
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
        }


        val body = response.bodyAsText()
        val message = Json.parseToJsonElement(body)
            .jsonObject["errors"]!!
            .jsonArray[0]
            .jsonObject["message"]!!
            .jsonPrimitive.content

        assertNotNull(body)
        assertEquals("Property name couldn't be empty", message)
    }


}