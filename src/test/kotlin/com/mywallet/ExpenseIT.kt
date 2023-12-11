package com.mywallet

import com.mywallet.application.expense.usecases.CreateExpenseUseCase
import com.mywallet.application.expense.usecases.QueryExpenseUseCase
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseDescription
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import com.mywallet.infrastructure.category.gateways.CategoryRepository
import com.mywallet.infrastructure.expense.gatways.CreateExpenseGatewayRepository
import com.mywallet.infrastructure.expense.gatways.ExpenseQueryRepository
import com.mywallet.infrastructure.expense.gatways.ValidationExpenseGateway
import com.mywallet.infrastructure.expense.graphql.ExpenseMutation
import com.mywallet.infrastructure.expense.graphql.ExpenseQuery
import com.mywallet.infrastructure.owner.gateways.CreateOwnerRepository
import com.mywallet.plugins.MyWalletIntegrationConfig
import com.mywallet.plugins.Neo4jConnection
import com.mywallet.plugins.asJsonPrimitive
import com.mywallet.plugins.httpGraphql
import com.mywallet.plugins.isValidUUID
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigDecimal
import java.time.LocalDate

class ExpenseIT : MyWalletIntegrationConfig() {

    private fun Application.setupModule() = myWalletModule {
        val connection = Neo4jConnection(connectionConfig)
        packages = listOf("com.mywallet")
        queries = listOf(ExpenseQuery(QueryExpenseUseCase(ExpenseQueryRepository(connection))))
        mutations = listOf(
            ExpenseMutation(
                useCase = CreateExpenseUseCase(
                    expenseValidation = ValidationExpenseGateway(),
                    expenseGatewayRepository = CreateExpenseGatewayRepository(connection)
                )
            )
        )
    }

    @Test
    fun `it should create a expense`() = testApplication {
        application { setupModule() }
        val connection = Neo4jConnection(connectionConfig)
        val owner = CreateOwnerRepository(connection).create(Owner(name = "Luis Camilo"))
        val category = CategoryRepository(connection).create(Category(name = "Restaurant"))
        val query = """
            mutation CreateExpense {
              createExpense(input: {
                category: { publicId: "${category.publicId}" },
                owner: { publicId: "${owner.publicId}" },
                price: {  value: 20.0, currencyMoney: "EUR" },
                description: { text: "Expended in Korean Restaurant" },
                expireDate: { date: "10/12/2023", format: "dd/MM/yyyy" },
                status: "PAID"
                type: "FIXED"
              }) { publicId }
            }
            """.asJsonPrimitive()

        val response = httpGraphql(query)
        val body = response.bodyAsText()
        val output = Json.parseToJsonElement(body)
        assertEquals(HttpStatusCode.OK, response.status)
        assertNull(output.jsonObject["errors"]?.jsonArray?.get(0), "should not contain any error")
        val expensePublicId = output.jsonObject["data"]
            ?.jsonObject?.get("createExpense")
            ?.jsonObject?.get("publicId")
            ?.jsonPrimitive?.content ?: ""
        assertTrue(isValidUUID(expensePublicId), "should contain a valid expense publicId")

    }

    @Test
    fun `it should return a list of expense paged`() = testApplication {
        application { setupModule() }
        val connection = Neo4jConnection(connectionConfig)
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

        val query = """
                query {
                    findExpenses(offset: 0, limit: 2) {
                        counter
                    }
                }
            """.asJsonPrimitive()

        val response = httpGraphql(query)
        val output = Json.parseToJsonElement(response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
        assertNull(output.jsonObject["errors"]?.jsonArray?.get(0), "should not contain any error")
    }


}