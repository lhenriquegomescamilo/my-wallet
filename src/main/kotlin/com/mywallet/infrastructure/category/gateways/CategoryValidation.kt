package com.mywallet.infrastructure.category.gateways

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import com.mywallet.application.category.gateways.CategoryValidationGateway
import com.mywallet.application.category.usecases.CategoryNameValidationError
import com.mywallet.application.category.usecases.CategoryPublicIdValidationError
import com.mywallet.application.category.usecases.CategoryValidationError
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ErrorMessage

class CategoryValidation : CategoryValidationGateway {
    override suspend fun validate(input: Category): Pair<List<ErrorMessage>, Category> {
        val result = this.validateCategory(input).mapLeft { validations ->
            val output = ArrayList<ErrorMessage>(validations.size)
            for (validation in validations) output.add(ErrorMessage(validation.name))
            output
        }
        return Pair(result.leftOrNull() ?: emptyList(), input)
    }

    private fun validateCategory(input: Category): Either<NonEmptyList<CategoryValidationError>, Category> = either {
        zipOrAccumulate(
            { ensure(input.name.isNotBlank()) { CategoryNameValidationError() } },
            { ensure(input.publicId.isNotBlank()) { CategoryPublicIdValidationError() } },

            ) { _, _ -> input }
    }
}