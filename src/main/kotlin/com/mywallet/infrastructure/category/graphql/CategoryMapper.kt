package com.mywallet.infrastructure.category.graphql

import com.mywallet.domain.entity.Category

fun CategoryInput.toDomainModel() = Category(name = this.name)

fun Category.toOutput() = CategoryOutput(publicId = publicId, name = name)