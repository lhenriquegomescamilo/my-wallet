package com.mywallet.application.category.gateways

import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ErrorMessage

interface CategoryValidationGateway {
    suspend fun validate(input: Category): Pair<List<ErrorMessage>, Category>
}
