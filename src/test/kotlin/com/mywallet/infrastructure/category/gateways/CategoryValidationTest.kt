package com.mywallet.infrastructure.category.gateways

import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ErrorMessage
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class CategoryValidationTest {
    @Test
    fun `it should validate a empty name`(): Unit = runBlocking {
        val categoryValidation = CategoryValidation()
        val result = categoryValidation.validate(Category(name = ""))
        assertTrue { result.first.isNotEmpty() }
        assertContentEquals(listOf(ErrorMessage(message = "Property name couldn't be empty")), result.first)
    }
}