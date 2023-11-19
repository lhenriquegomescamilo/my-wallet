package com.mywallet.application.owner.usecases

import com.mywallet.application.UseCase
import com.mywallet.application.ValidationGateway
import com.mywallet.application.owner.gateways.CreateOwnerGateway
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.ValidationError

class CreateOwnerUseCase(
    private val createOwnerGateway: CreateOwnerGateway,
    private val validateOwnerGateway: ValidationGateway<Owner>
) : UseCase<Owner>() {
    override suspend fun execute(input: Owner): Result<Owner> {
        val validations = validateOwnerGateway.validate(input)
        if (validations.first.isNotEmpty()) {
            return Result.failure(ValidationError(validations.first))
        }
        return runCatching { createOwnerGateway.save(input) }
    }

}