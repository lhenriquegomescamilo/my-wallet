package com.mywallet.infrastructure.owner.gateways

import com.mywallet.domain.entity.Owner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking


class ValidateOwnerGatewayTest {

    @Test
    fun `it should not return an error when the name of owner is fully`(): Unit = runBlocking {
        val input = Owner(name = "Luis Camilo", publicId = "74646d39-3899-47f0-9b5a-3b031b06db37")
        val result = ValidateOwnerGateway().validate(input)
        assertTrue { result.first.isEmpty() }
    }


    @Test
    fun `it should not return an error when the name of owner is fully without publicId`(): Unit = runBlocking {
        val input = Owner(name = "Luis Camilo", publicId = "")
        val result = ValidateOwnerGateway().validate(input)
        assertTrue { result.first.isEmpty() }
    }

    @Test
    fun `it should return an error when the name of owner is empty`(): Unit = runBlocking {
        val input = Owner(name = "")
        val result = ValidateOwnerGateway().validate(input)
        assertTrue { result.first.isNotEmpty() }
        assertEquals(result.first.first().key, "name")
    }
}