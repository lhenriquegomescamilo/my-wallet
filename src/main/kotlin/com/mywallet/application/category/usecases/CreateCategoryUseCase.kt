package com.mywallet.application.category.usecases

import com.mywallet.application.UseCase
import com.mywallet.application.category.gateways.CategoryRepositoryGateway
import com.mywallet.application.category.gateways.CategoryValidationGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ValidationError

class CreateCategoryUseCase(
    private val categoryRepositoryGateway: CategoryRepositoryGateway,
    private val categoryValidationGateway: CategoryValidationGateway
) : UseCase<Category>() {

    override suspend fun execute(input: Category): Result<Category> {
        val (validations) = categoryValidationGateway.validate(input)

        if (validations.isNotEmpty()) {
            return Result.failure(ValidationError(validations))
        }
        if (categoryRepositoryGateway.checkIfExists(input)) {
            return Result.failure(Exception("The category ${input.name} already exists"))
        }
        return Result.success(categoryRepositoryGateway.create(input))
    }

}

sealed class CategoryValidationError(open val name: String)
data class CategoryNameValidationError(override val name: String = "Property name couldn't be empty") :
    CategoryValidationError(name)

data class CategoryPublicIdValidationError(override val name: String = "Property publicId couldn't be empty") :
    CategoryValidationError(name)