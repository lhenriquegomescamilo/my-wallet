package com.mywallet.infrastructure.category.graphql

import com.expediagroup.graphql.server.operations.Query

class CategoryQuery : Query {
    fun findAllCategories(): List<CategoryOutput> = emptyList()
}
