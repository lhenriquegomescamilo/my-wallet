package com.mywallet.infrastructure.category.gateways

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import com.mywallet.application.ValidationGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.CategoryNameValidationError
import com.mywallet.domain.entity.CategoryPublicIdValidationError
import com.mywallet.domain.entity.CategoryValidationError
import com.mywallet.domain.entity.ErrorMessage

class CategoryValidation : ValidationGateway<Category> {
    override suspend fun validate(input: Category): Pair<List<ErrorMessage>, Category> {
        val result = validateCategory(input).mapLeft { validations ->
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