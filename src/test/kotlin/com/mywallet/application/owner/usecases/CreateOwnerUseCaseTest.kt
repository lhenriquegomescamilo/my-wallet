package com.mywallet.application.owner.usecases

import com.mywallet.application.owner.gateways.CreateOwnerRepositoryGateway
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.ValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import java.util.*


class CreateOwnerUseCaseTest {

    @Test
    fun `it should create a owner`(): Unit = runBlocking {
        val input = Owner(name = "Luis Camilo", publicId = "")
        val createOwnerRepositoryGateway = object : CreateOwnerRepositoryGateway {
            override suspend fun create(input: Owner): Owner {
                return input.copy(publicId = UUID.randomUUID().toString())
            }
        }
        val result = CreateOwnerUseCase(createOwnerRepositoryGateway,).execute(input)
        assertTrue { result.isSuccess }
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `it should return a error when the name of owner is empty`(): Unit = runBlocking {
        val input = Owner(name = "", publicId = "")
        val createOwnerRepositoryGateway = object : CreateOwnerRepositoryGateway {
            override suspend fun create(input: Owner): Owner {
                return input.copy(publicId = UUID.randomUUID().toString())
            }
        }
        val result = CreateOwnerUseCase(createOwnerRepositoryGateway).execute(input)
        assertTrue { result.isFailure }
        assertNotNull(result.exceptionOrNull())
        assertIs<ValidationError>(result.exceptionOrNull())
        val (validations) = result.exceptionOrNull() as ValidationError
        assertEquals("name", validations.first().key)
    }


    @Test
    fun `it should not return an error when the name of owner is fully`(): Unit = runBlocking {
        val input = Owner(name = "Luis Camilo", publicId = "74646d39-3899-47f0-9b5a-3b031b06db37")
        val createOwnerRepositoryGateway = object : CreateOwnerRepositoryGateway {
            override suspend fun create(input: Owner): Owner {
                return input.copy(publicId = UUID.randomUUID().toString())
            }
        }
        val result = CreateOwnerUseCase(createOwnerRepositoryGateway).execute(input)
        assertTrue { result.isSuccess }
    }


    @Test
    fun `it should not return an error when the name of owner is fully without publicId`(): Unit = runBlocking {
        val input = Owner(name = "Luis Camilo", publicId = "")
        val createOwnerRepositoryGateway = object : CreateOwnerRepositoryGateway {
            override suspend fun create(input: Owner): Owner {
                return input.copy(publicId = UUID.randomUUID().toString())
            }
        }
        val result = CreateOwnerUseCase(createOwnerRepositoryGateway).execute(input)
        assertTrue { result.isSuccess }
    }

    @Test
    fun `it should return an error when the name of owner is empty`(): Unit = runBlocking {
        val input = Owner(name = "")
        val createOwnerRepositoryGateway = object : CreateOwnerRepositoryGateway {
            override suspend fun create(input: Owner): Owner {
                return input.copy(publicId = UUID.randomUUID().toString())
            }
        }
        val result = CreateOwnerUseCase(createOwnerRepositoryGateway).execute(input)

        assertTrue { result.isFailure }
        val errorMessage = result.exceptionOrNull()?.let { it as ValidationError }
        assertNotNull(errorMessage)
        assertEquals("name", errorMessage.validations.first().key)
    }
}