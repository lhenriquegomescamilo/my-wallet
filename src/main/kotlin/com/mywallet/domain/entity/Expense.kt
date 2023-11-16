package com.mywallet.domain.entity

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Price(val value: BigDecimal, val currencyMoney: String) {}

data class Owner(val publicId: String = UUID.randomUUID().toString(), val name: String)

enum class ExpenseType {
    FIXED, VARIABLE, EMPTY;
}

enum class ExpenseStatus {
    PAID, NOT_PAID, EMPTY;
}

data class ExpenseDescription(val description: String) {

}

data class Expense(
    val publicId: String = UUID.randomUUID().toString(),
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

sealed class ExpenseValidation(open val name: String = "", open val key: String = "") {
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
}

