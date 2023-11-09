package com.mywallet.domain.entity

import java.math.BigDecimal
import java.time.LocalDate
import java.util.Currency
import java.util.Date
import java.util.UUID

data class Price(val price: BigDecimal, val currencyMoney: String) {}

data class Owner(val publicId: String = UUID.randomUUID().toString(), val name: String) {}

enum class ExpenseType {
    FIXED, VARIABLE;
}
enum class ExpenseStatus {
    PAID, NOT_PAID;
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
) {
}
