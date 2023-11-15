package com.mywallet.infrastructure.expense.gatways

import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import com.mywallet.application.ValidationGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ErrorMessage
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseValidation
import com.mywallet.domain.entity.ExpenseValidation.CategoryPublicIdError
import com.mywallet.domain.entity.Price
import java.util.LinkedList
import java.math.BigDecimal

class ValidationExpenseGateway : ValidationGateway<Expense> {
    override suspend fun validate(input: Expense): Pair<List<ErrorMessage>, Expense> {
        val validationErrors = LinkedList<ErrorMessage>()

        validateCategory(input.category).mapLeft { ErrorMessage(it.name, it.key) }.leftOrNull()
            ?.let(validationErrors::add)

        validatePrice(input.price).mapLeft { validations ->
            for (validation in validations) validationErrors.add(ErrorMessage(validation.name, validation.key))
        }
        return Pair(validationErrors, input)
    }

    private fun validateCategory(category: Category) = either<ExpenseValidation, Category> {
        ensure(category.publicId.isNotEmpty()) { CategoryPublicIdError() }
        category
    }

    private fun validatePrice(price: Price) = either<NonEmptyList<ExpenseValidation>, Price> {
        zipOrAccumulate(
            { ensure(price.value != BigDecimal.ZERO) { ExpenseValidation.PriceValueValidation() } },
            { ensure(price.currencyMoney.isNotEmpty()) { ExpenseValidation.PriceCurrencyValidation() } }
        ) { _, _ -> price }
    }
}