package com.mywallet.application.category.gateways

import com.mywallet.domain.entity.Category

interface CategoryRepositoryGateway {
    suspend fun create(category: Category): Category
    suspend fun checkIfExists(category: Category): Boolean

}
