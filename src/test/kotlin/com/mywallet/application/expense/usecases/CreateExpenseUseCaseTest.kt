package com.mywallet.application.expense.usecases


import com.mywallet.application.ValidationGateway
import com.mywallet.application.expense.gateways.CreateExpenseGateway
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.ErrorMessage
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseDescription
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import com.mywallet.infrastructure.expense.gatways.ValidationExpenseGateway
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
            price = Price(value = BigDecimal.valueOf(100.0), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo"),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription("Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )

        val createExpenseGateway = object : CreateExpenseGateway {
            override suspend fun create(expense: Expense) = expense
        }

        val validationGateway = object : ValidationGateway<Expense> {
            override suspend fun validate(input: Expense): Pair<List<ErrorMessage>, Expense> {
                return Pair(emptyList(), input)
            }

        }

        val result = CreateExpenseUseCase(createExpenseGateway, validationGateway).execute(expense)
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `it should return a category error when the publicId is not defined`(): Unit = runBlocking {
        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = ""),
            price = Price(value = BigDecimal.valueOf(100.0), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo"),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription("Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )

        val createExpenseGateway = object : CreateExpenseGateway {
            override suspend fun create(expense: Expense) = expense
        }

        val validationGateway = ValidationExpenseGateway()

        val result = CreateExpenseUseCase(createExpenseGateway, validationGateway).execute(expense)
        assertTrue { result.isFailure }
    }
}