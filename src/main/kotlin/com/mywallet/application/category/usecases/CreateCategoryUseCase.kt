package com.mywallet.application.category.usecases

import com.mywallet.application.UseCase
import com.mywallet.application.category.gateways.CategoryRepositoryGateway
import com.mywallet.application.ValidationGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ValidationError

class CreateCategoryUseCase(
    private val categoryRepositoryGateway: CategoryRepositoryGateway,
    private val validationGateway: ValidationGateway<Category>
) : UseCase<Category>() {

    override suspend fun execute(input: Category): Result<Category> {
        val (validations) = validationGateway.validate(input)

        if (validations.isNotEmpty()) {
            return Result.failure(ValidationError(validations))
        }
        if (categoryRepositoryGateway.checkIfExists(input)) {
            return Result.failure(Exception("The category ${input.name} already exists"))
        }
        return runCatching { categoryRepositoryGateway.create(input) }
    }

}

