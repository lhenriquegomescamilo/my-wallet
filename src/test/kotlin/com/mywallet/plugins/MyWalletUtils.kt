package com.mywallet.plugins

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.util.*

fun String.asJsonPrimitive(): JsonPrimitive = JsonPrimitive(this)

fun isValidUUID(input: String): Boolean {
    return runCatching { UUID.fromString(input) }.isSuccess
}

suspend fun ApplicationTestBuilder.httpGraphql(query: JsonPrimitive): HttpResponse {
    val bodyRequest = buildJsonObject {
        put("query", query)
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
    return response
}