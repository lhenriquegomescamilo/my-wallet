package com.mywallet.domain.entity

import com.mywallet.infrastructure.category.graphql.CategoryOutput
import java.util.*

data class Category(val publicId: String = UUID.randomUUID().toString(), val name: String) {
    fun asOutput(): CategoryOutput = CategoryOutput(this.publicId, this.name)
}

sealed class CategoryValidationError(open val name: String)
data class CategoryNameValidationError(override val name: String = "Property name couldn't be empty") :
    CategoryValidationError(name)

data class CategoryPublicIdValidationError(override val name: String = "Property publicId couldn't be empty") :
    CategoryValidationError(name)