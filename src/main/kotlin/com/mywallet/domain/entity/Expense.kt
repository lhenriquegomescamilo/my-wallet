package com.mywallet.domain.entity

import com.mywallet.infrastructure.expense.graphql.PriceOutput
import java.math.BigDecimal
import java.time.LocalDate

data class Price(val value: BigDecimal, val currencyMoney: String) {
    fun asOutput(): PriceOutput = PriceOutput(this.value.toDouble(), this.currencyMoney)
}

enum class ExpenseType {
    FIXED, VARIABLE, EMPTY;

    companion object {
        fun byNameIgnoreCaseOrEmpty(input: String): ExpenseType {
            return entries.firstOrNull { it.name.equals(input, true) } ?: EMPTY
        }
    }
}

enum class ExpenseStatus {
    PAID, NOT_PAID, EMPTY;

    companion object {
        fun byNameIgnoreCaseOrEmpty(input: String): ExpenseStatus =
            entries.firstOrNull { it.name.equals(input, true) } ?: EMPTY
    }
}

data class ExpenseDescription(val text: String)

data class Expense(
    val publicId: String = "",
    val category: Category,
    val price: Price,
    val owner: Owner,
    val type: ExpenseType,
    val status: ExpenseStatus,
    val description: ExpenseDescription,
    val expireDate: LocalDate,
    val paymentDate: LocalDate? = null
)

typealias ErrorsOrExpense = Pair<List<ErrorMessage>, Expense>

sealed class ExpenseValidation(override val name: String = "", override val key: String = "") : Constraints(name, key) {
    class CategoryPublicIdError(
        override val name: String = "Property publicId couldn't be empty",
        override val key: String = "category.publicId"
    ) : ExpenseValidation(name, key)

    class PriceValueValidation(
        override val name: String = "Property price.value could not be zero",
        override val key: String = "price.value"
    ) : ExpenseValidation(name, key)

    class PriceCurrencyValidation(
        override val name: String = "Property price.currency could not be empty",
        override val key: String = "price.currencyMoney"
    ) : ExpenseValidation(name, key)

    class OwnerPublicIdValidation(
        override val name: String = "Property owner.publicId could not be empty",
        override val key: String = "owner.publicId"
    ) : ExpenseValidation(name, key)


    class TypeValidation(
        override val name: String = "Property type could not be empty",
        override val key: String = "type"
    ) : ExpenseValidation(name, key)


    class StatusValidation(
        override val name: String = "Property status could not be empty",
        override val key: String = "status"
    ) : ExpenseValidation(name, key)

    class InvalidPaymentDate(
        override val name: String = "Invalid payment date",
        override val key: String = "paymentDate"
    ) : ExpenseValidation(name, key)

}

