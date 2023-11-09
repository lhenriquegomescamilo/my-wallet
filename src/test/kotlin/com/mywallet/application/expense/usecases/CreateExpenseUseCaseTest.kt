package com.mywallet.application.expense.usecases


import com.mywallet.application.expense.gateways.CreateExpenseGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseDescription
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate

class CreateExpenseUseCaseTest {

    @Test
    fun `it should create expense `(): Unit = runBlocking {
        val expense = Expense(
            category = Category(name = "Personal Trainer"),
            price = Price(price = BigDecimal.valueOf(100.0), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo"),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription("Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )

        val createExpenseGateway = object : CreateExpenseGateway {
            override suspend fun create(expense: Expense) = expense
            override suspend fun checkIfExists(input: Expense) = false
        }

        val result = CreateExpenseUseCase(createExpenseGateway).execute(expense)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `it should an error when fixed expense already exists `(): Unit = runBlocking {
        val expense = Expense(
            category = Category(name = "Personal Trainer"),
            price = Price(price = BigDecimal.valueOf(100.0), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo"),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription("Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )

        val createExpenseGateway = object : CreateExpenseGateway {
            override suspend fun create(expense: Expense) = expense
            override suspend fun checkIfExists(input: Expense) = true
        }

        val result = CreateExpenseUseCase(createExpenseGateway).execute(expense)
        assertTrue(result.isFailure)
    }

}