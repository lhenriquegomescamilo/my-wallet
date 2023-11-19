package com.mywallet.application.owner.usecases

import com.mywallet.application.ValidationGateway
import com.mywallet.application.owner.gateways.CreateOwnerGateway
import com.mywallet.domain.entity.ErrorMessage
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.OwnerConstraint
import com.mywallet.domain.entity.ValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import java.util.UUID


class CreateOwnerUseCaseTest {

    @Test
    fun `it should create a owner`(): Unit = runBlocking {
        val input = Owner(name = "Luis Camilo", publicId = "")
        val createOwnerGateway = object : CreateOwnerGateway {
            override suspend fun save(input: Owner): Owner {
                return input.copy(publicId = UUID.randomUUID().toString())
            }
        }
        val validationGateway = object : ValidationGateway<Owner> {
            override suspend fun validate(input: Owner): Pair<List<ErrorMessage>, Owner> {
                return Pair(emptyList(), input)
            }

        }
        val result = CreateOwnerUseCase(createOwnerGateway, validationGateway).execute(input)
        assertTrue { result.isSuccess }
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `it should return a error when the name of owner is empty`(): Unit = runBlocking {
        val input = Owner(name = "Luis Camilo", publicId = "")
        val createOwnerGateway = object : CreateOwnerGateway {
            override suspend fun save(input: Owner): Owner {
                return input.copy(publicId = UUID.randomUUID().toString())
            }
        }
        val validationGateway = object : ValidationGateway<Owner> {
            override suspend fun validate(input: Owner): Pair<List<ErrorMessage>, Owner> {
                val constraint = OwnerConstraint.OwnerEmptyNameConstraints()
                val errors = listOf(
                    ErrorMessage(constraint.name, constraint.key)
                )
                return Pair(errors, input)
            }

        }
        val result = CreateOwnerUseCase(createOwnerGateway, validationGateway).execute(input)
        assertTrue { result.isFailure }
        assertNotNull(result.exceptionOrNull())
        assertIs<ValidationError>(result.exceptionOrNull())
        val (validations) = result.exceptionOrNull() as ValidationError
        assertEquals("name", validations.first().key)
    }
}