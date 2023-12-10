package com.mywallet

import com.mywallet.application.expense.usecases.CreateExpenseUseCase
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.Owner
import com.mywallet.infrastructure.category.gateways.CategoryRepository
import com.mywallet.infrastructure.expense.gatways.CreateExpenseGatewayRepository
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ExpenseIT : MyWalletIntegrationConfig() {

    private fun Application.setupModule() = myWalletModule {
        packages = listOf("com.mywallet")
        queries = listOf(ExpenseQuery())
        mutations = listOf(
            ExpenseMutation(
                useCase = CreateExpenseUseCase(
                    expenseValidation = ValidationExpenseGateway(),
                    expenseGatewayRepository = CreateExpenseGatewayRepository(Neo4jConnection(connectionConfig))
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
            ?.jsonPrimitive?.content
        assertNotNull(expensePublicId)
        assertTrue(isValidUUID(expensePublicId), "should contain a valid expense public id")

    }

}