package com.mywallet.infrastructure.expense

import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseDescription
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import com.mywallet.infrastructure.expense.gatways.ValidationExpenseGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate


class ValidationExpenseGatewayTest {


    @Test
    fun `it should return an error when the  publicId of category is empty`(): Unit = runBlocking {

        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = ""),
            price = Price(value = BigDecimal.valueOf(100.0), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo", publicId = "e175755a-9fca-4d0a-84b3-d74d87998c63"),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription(text = "Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )
        val output = ValidationExpenseGateway().validate(expense)
        assertTrue { output.first.isNotEmpty() }
        assertEquals(1, output.first.size)
        assertEquals("category.publicId", output.first.first().key)

    }


    @Test
    fun `it should validate an empty publicId of category and price is ZERO`(): Unit = runBlocking {

        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = ""),
            price = Price(value = BigDecimal.ZERO, currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo", publicId = "e175755a-9fca-4d0a-84b3-d74d87998c63"),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription(text = "Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )
        val expenseValidation = ValidationExpenseGateway()
        val output = expenseValidation.validate(expense)
        assertTrue { output.first.isNotEmpty() }
        assertEquals(2, output.first.size)
        assertEquals("price.value", output.first[output.first.lastIndex].key)

    }

    @Test
    fun `it should validate an empty publicId of category and price is empty currency`(): Unit = runBlocking {

        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = ""),
            price = Price(value = BigDecimal.valueOf(20.12), currencyMoney = ""),
            owner = Owner(name = "Luis Camilo", publicId = "e175755a-9fca-4d0a-84b3-d74d87998c63"),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription(text = "Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )
        val expenseValidation = ValidationExpenseGateway()
        val output = expenseValidation.validate(expense)
        assertTrue { output.first.isNotEmpty() }
        assertEquals(2, output.first.size)
        assertEquals("price.currencyMoney", output.first[output.first.lastIndex].key)

    }

    @Test
    fun `it should validate if owner not has publicId`(): Unit = runBlocking {

        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = "b41b1817-5a97-43f6-bb68-a01bb0fb962f"),
            price = Price(value = BigDecimal.valueOf(20.12), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo", publicId = ""),
            type = ExpenseType.FIXED,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription(text = "Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )
        val expenseValidation = ValidationExpenseGateway()
        val output = expenseValidation.validate(expense)
        assertTrue { output.first.isNotEmpty() }
        assertEquals(1, output.first.size)
        assertEquals("owner.publicId", output.first.first().key)

    }

    @Test
    fun `it should validate returning error when type is empty`(): Unit = runBlocking {

        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = "b41b1817-5a97-43f6-bb68-a01bb0fb962f"),
            price = Price(value = BigDecimal.valueOf(20.12), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo", publicId = "5a21deb3-27b2-47e2-9d58-c20b236d6381"),
            type = ExpenseType.EMPTY,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription(text = "Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )
        val expenseValidation = ValidationExpenseGateway()
        val output = expenseValidation.validate(expense)
        assertTrue { output.first.isNotEmpty() }
        assertEquals(1, output.first.size)
        assertEquals("type", output.first.first().key)
    }


    @Test
    fun `it should validate returning error when status is empty`(): Unit = runBlocking {

        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = "b41b1817-5a97-43f6-bb68-a01bb0fb962f"),
            price = Price(value = BigDecimal.valueOf(20.12), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo", publicId = "5a21deb3-27b2-47e2-9d58-c20b236d6381"),
            type = ExpenseType.VARIABLE,
            status = ExpenseStatus.EMPTY,
            description = ExpenseDescription(text = "Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = null
        )
        val expenseValidation = ValidationExpenseGateway()
        val output = expenseValidation.validate(expense)
        assertTrue { output.first.isNotEmpty() }
        assertEquals(1, output.first.size)
        assertEquals("status", output.first.first().key)
    }

    @Test
    fun `it should return a error when payment date is defined and expense status is not paid`(): Unit = runBlocking {

        val expense = Expense(
            category = Category(name = "Personal Trainer", publicId = "b41b1817-5a97-43f6-bb68-a01bb0fb962f"),
            price = Price(value = BigDecimal.valueOf(20.12), currencyMoney = "EUR"),
            owner = Owner(name = "Luis Camilo", publicId = "5a21deb3-27b2-47e2-9d58-c20b236d6381"),
            type = ExpenseType.VARIABLE,
            status = ExpenseStatus.NOT_PAID,
            description = ExpenseDescription(text = "Box"),
            expireDate = LocalDate.now().plusDays(20),
            paymentDate = LocalDate.now()
        )
        val expenseValidation = ValidationExpenseGateway()
        val output = expenseValidation.validate(expense)
        assertTrue { output.first.isNotEmpty() }
        assertEquals(1, output.first.size)
        assertEquals("paymentDate", output.first.first().key)
    }


}