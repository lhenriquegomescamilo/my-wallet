package com.mywallet.application.category.gateways

import com.mywallet.domain.entity.Category

interface CategoryGateway {
    suspend fun create(category: Category): Category
    suspend fun checkIfExists(category: Category): Boolean

}
