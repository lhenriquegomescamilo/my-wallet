package com.mywallet.application.category.usecases

import com.mywallet.application.UseCase
import com.mywallet.application.category.gateways.CategoryGateway
import com.mywallet.domain.entity.Category

class CreateCategoryUseCase(private val categoryGateway: CategoryGateway) : UseCase<Category>() {

    override suspend fun execute(category: Category): Result<Category> {
        if (category.name.isEmpty()) {
            return Result.failure(Exception("Property name couldn't be empty"))
        }
        if (categoryGateway.checkIfExists(category)) {
            return Result.failure(Exception("The category ${category.name} already exists"))
        }
        return Result.success(categoryGateway.create(category))
    }

}