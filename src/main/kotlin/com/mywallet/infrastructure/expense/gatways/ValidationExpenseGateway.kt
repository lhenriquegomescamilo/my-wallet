package com.mywallet.infrastructure.expense.gatways

import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import com.mywallet.application.ValidationGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ErrorsOrExpense
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.ExpenseValidation
import com.mywallet.domain.entity.ExpenseValidation.CategoryPublicIdError
import com.mywallet.domain.entity.ExpenseValidation.InvalidPaymentDate
import com.mywallet.domain.entity.ExpenseValidation.OwnerPublicIdValidation
import com.mywallet.domain.entity.ExpenseValidation.PriceCurrencyValidation
import com.mywallet.domain.entity.ExpenseValidation.PriceValueValidation
import com.mywallet.domain.entity.ExpenseValidation.StatusValidation
import com.mywallet.domain.entity.ExpenseValidation.TypeValidation
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import com.mywallet.infrastructure.utils.leftOrEmpty
import com.mywallet.infrastructure.utils.runConstraints
import java.math.BigDecimal

class ValidationExpenseGateway : ValidationGateway<Expense> {
    override suspend fun validate(input: Expense): ErrorsOrExpense {
        return runConstraints(input) {
            validateCategory(input.category).leftOrEmpty() +
                    validateOwner(input.owner).leftOrEmpty() +
                    (validatePrice(input.price).leftOrNull() ?: emptyList()) +
                    validateType(input.type).leftOrEmpty() +
                    (validateStatus(input.status).leftOrEmpty()) +
                    validatePaymentDate(input).leftOrEmpty()
        }
    }


    private fun validateCategory(category: Category) = either {
        ensure(category.publicId.isNotEmpty()) { CategoryPublicIdError() }
        category
    }

    private fun validatePrice(price: Price) = either<NonEmptyList<ExpenseValidation>, Price> {
        zipOrAccumulate(
            { ensure(price.value != BigDecimal.ZERO) { PriceValueValidation() } },
            { ensure(price.currencyMoney.isNotEmpty()) { PriceCurrencyValidation() } }
        ) { _, _ -> price }
    }

    private fun validateOwner(owner: Owner) = either {
        ensure(owner.publicId.isNotEmpty()) { OwnerPublicIdValidation() }
        owner
    }

    private fun validateType(type: ExpenseType) = either {
        ensure(type != ExpenseType.EMPTY) { TypeValidation() }
        type
    }

    private fun validateStatus(status: ExpenseStatus) = either {
        ensure(status != ExpenseStatus.EMPTY) { StatusValidation() }
        status
    }

    private fun validatePaymentDate(expense: Expense) = either {
        if (expense.paymentDate != null) {
            ensure(expense.status != ExpenseStatus.NOT_PAID) { InvalidPaymentDate() }
        }
        expense
    }
}
