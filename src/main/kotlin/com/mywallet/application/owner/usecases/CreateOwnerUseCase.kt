package com.mywallet.application.owner.usecases

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.mywallet.application.UseCase
import com.mywallet.application.owner.gateways.CreateOwnerRepositoryGateway
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.OwnerConstraint
import com.mywallet.domain.entity.ValidationError
import com.mywallet.infrastructure.utils.leftOrEmpty
import com.mywallet.infrastructure.utils.runConstraints

class CreateOwnerUseCase(
    private val createOwnerRepositoryGateway: CreateOwnerRepositoryGateway,
) : UseCase<Owner>() {
    override suspend fun execute(input: Owner): Result<Owner> {
        val (errorsMessages) = validate(input)
        if (errorsMessages.isNotEmpty()) {
            return Result.failure(ValidationError(errorsMessages))
        }
        return runCatching { createOwnerRepositoryGateway.create(input) }
    }

    private fun validate(input: Owner) = runConstraints(input) {
        validateName(input).leftOrEmpty()
    }

    private fun validateName(input: Owner) = either {
        ensure(input.name.isNotEmpty()) { OwnerConstraint.OwnerEmptyNameConstraints() }
        input
    }

}