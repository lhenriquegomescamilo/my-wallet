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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.buildJsonObject

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


        val bodyRequest = buildJsonObject {
            put("query", query)
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(bodyRequest)
        }

        assertEquals(HttpStatusCode.OK, response.status)

    }

}