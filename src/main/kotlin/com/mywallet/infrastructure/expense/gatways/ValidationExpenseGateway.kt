package com.mywallet.infrastructure.expense.gatways

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import com.mywallet.application.ValidationGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ErrorMessage
import com.mywallet.domain.entity.ErrorsOrExpense
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.ExpenseValidation
import com.mywallet.domain.entity.ExpenseValidation.CategoryPublicIdError
import com.mywallet.domain.entity.ExpenseValidation.OwnerPublicIdValidation
import com.mywallet.domain.entity.ExpenseValidation.PriceCurrencyValidation
import com.mywallet.domain.entity.ExpenseValidation.PriceValueValidation
import com.mywallet.domain.entity.ExpenseValidation.StatusValidation
import com.mywallet.domain.entity.ExpenseValidation.TypeValidation
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import java.math.BigDecimal

class ValidationExpenseGateway : ValidationGateway<Expense> {
    override suspend fun validate(input: Expense): ErrorsOrExpense {
        return listOf(
            validateCategory(input.category).leftOrEmpty(),
            validateOwner(input.owner).leftOrEmpty(),
            validatePrice(input.price).leftOrNull() ?: emptyList(),
            validateType(input.type).leftOrEmpty(),
            validateStatus(input.status).leftOrEmpty()
        )
            .flatMap { validations -> validations.map { ErrorMessage(it.name, it.key) } }
            .let { ErrorsOrExpense(it, input) }
    }

    private fun <T> Either<ExpenseValidation, T>.leftOrEmpty() = this.leftOrNull()?.let(::listOf) ?: emptyList()

    private fun validateCategory(category: Category) = either<ExpenseValidation, Category> {
        ensure(category.publicId.isNotEmpty()) { CategoryPublicIdError() }
        category
    }

    private fun validatePrice(price: Price) = either<NonEmptyList<ExpenseValidation>, Price> {
        zipOrAccumulate(
            { ensure(price.value != BigDecimal.ZERO) { PriceValueValidation() } },
            { ensure(price.currencyMoney.isNotEmpty()) { PriceCurrencyValidation() } }
        ) { _, _ -> price }
    }

    private fun validateOwner(owner: Owner) = either<ExpenseValidation, Owner> {
        ensure(owner.publicId.isNotEmpty()) { OwnerPublicIdValidation() }
        owner
    }

    private fun validateType(type: ExpenseType) = either<ExpenseValidation, ExpenseType> {
        ensure(type != ExpenseType.EMPTY) { TypeValidation() }
        type
    }

    private fun validateStatus(status: ExpenseStatus) = either<ExpenseValidation, ExpenseStatus> {
        ensure(status != ExpenseStatus.EMPTY) { StatusValidation() }
        status
    }
}
