package com.mywallet.infrastructure.expense.gatways

import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseDescription
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import com.mywallet.infrastructure.category.gateways.CategoryRepository
import com.mywallet.infrastructure.owner.gateways.CreateOwnerRepository
import com.mywallet.plugins.MyWalletIntegrationConfig
import com.mywallet.plugins.Neo4jConnection
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate


class ExpenseQueryRepositoryTest : MyWalletIntegrationConfig() {

    @Test
    fun `it should 2 first expenses using page strategy`() = testApplication {
        createExpenses(Neo4jConnection(connectionConfig))

        val expenseRepository = ExpenseQueryRepository(Neo4jConnection(connectionConfig))
        val result = expenseRepository.findExpensesPaged(0, 10)
        assertEquals(result.counter, 2)
    }

    private fun createExpenses(connection: Neo4jConnection) = runBlocking {
        val camilo = CreateOwnerRepository(connection).create(Owner(name = "Luis Camilo"))
        val restaurant = CategoryRepository(connection).create(Category(name = "Restaurant"))
        val delivery = CategoryRepository(connection).create(Category(name = "Delivery"))
        val expenseRepository = CreateExpenseGatewayRepository(connection)
        listOf(
            Expense(
                category = restaurant,
                owner = camilo,
                price = Price(value = BigDecimal.valueOf(100.0), currencyMoney = "EUR"),
                type = ExpenseType.FIXED,
                status = ExpenseStatus.NOT_PAID,
                description = ExpenseDescription(text = "Box"),
                expireDate = LocalDate.now().plusDays(20),
                paymentDate = null
            ),
            Expense(
                category = delivery,
                owner = camilo,
                price = Price(value = BigDecimal.valueOf(100.0), currencyMoney = "EUR"),
                type = ExpenseType.FIXED,
                status = ExpenseStatus.NOT_PAID,
                description = ExpenseDescription(text = "Box"),
                expireDate = LocalDate.now().plusDays(20),
                paymentDate = null
            )
        ).forEach { expenseRepository.create(it) }
    }

}