package com.mywallet.infrastructure.category.graphql

data class CategoryInput(val name: String)

data class CategoryOutput(val publicId: String, val name: String)
