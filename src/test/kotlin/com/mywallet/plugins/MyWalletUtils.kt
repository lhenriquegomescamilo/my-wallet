package com.mywallet.plugins

import kotlinx.serialization.json.JsonPrimitive
import java.util.UUID

fun String.asJsonPrimitive(): JsonPrimitive = JsonPrimitive(this)

fun isValidUUID(input: String): Boolean {
    return runCatching { UUID.fromString(input) }.isSuccess
}