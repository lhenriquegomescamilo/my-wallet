package com.mywallet.domain.entity

import java.util.UUID

data class Category(val publicId: String = UUID.randomUUID().toString(), val name: String)
sealed class CategoryValidationError(open val name: String)
data class CategoryNameValidationError(override val name: String = "Property name couldn't be empty") :
    CategoryValidationError(name)

data class CategoryPublicIdValidationError(override val name: String = "Property publicId couldn't be empty") :
    CategoryValidationError(name)