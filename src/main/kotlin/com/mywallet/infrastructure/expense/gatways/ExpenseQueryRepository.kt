package com.mywallet.infrastructure.expense.gatways

import com.mywallet.DatabaseConnection
import com.mywallet.application.expense.usecases.ExpenseQueryGateway
import com.mywallet.application.expense.usecases.Page
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseDescription
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import org.neo4j.cypherdsl.core.Cypher
import org.neo4j.cypherdsl.core.Cypher.match
import org.neo4j.cypherdsl.core.Cypher.node
import org.neo4j.cypherdsl.core.Functions
import org.neo4j.driver.Record
import org.neo4j.driver.Session
import java.math.BigDecimal
import java.time.LocalDate

class ExpenseQueryRepository(private val connection: DatabaseConnection<Session>) : ExpenseQueryGateway {
    override fun findExpensesPaged(offset: Int, limit: Int): Page<Expense> =
        connection.session.executeRead { transaction ->
            val category = node("Category").named("category")
            val owner = node("Owner").named("owner")
            val expandedIn = owner.relationshipTo(category, "EXPENDED_IN").named("expendedIn")
            val query = match(expandedIn)
                .returning(owner, expandedIn, category)
                .skip(offset)
                .limit(limit)
                .build()

            val result = transaction.run(query.cypher)
            val output = result.list()
            val expenses = output.map(::recordToExpense)

            val statement = match(expandedIn).returning(Functions.count(Cypher.asterisk())).build()
            val resultCounter = transaction.run(statement.cypher)
            val counter = resultCounter.single().get(0).asInt()
            Page(data = expenses, counter = counter)
        }

    private fun recordToExpense(record: Record): Expense {
        val categoryRecord = record.get("category")
        val expenseIn = record.get("expendedIn")
        val ownerRecord = record.get("owner")
        return Expense(
            publicId = expenseIn.get("publicId").asString(),
            category = Category(
                publicId = categoryRecord.get("publicId").asString(),
                name = categoryRecord.get("name").asString()
            ),
            price = Price(value = BigDecimal.valueOf(expenseIn.get("price").asDouble()), currencyMoney = "EUR"),
            owner = Owner(
                publicId = ownerRecord.get("publicId").asString(),
                name = ownerRecord.get("name").asString()
            ),
            type = ExpenseType.byNameIgnoreCaseOrEmpty(expenseIn.get("type").asString()),
            description = ExpenseDescription(""),
            expireDate = LocalDate.now(),
            status = ExpenseStatus.byNameIgnoreCaseOrEmpty(expenseIn.get("status").asString()),
            paymentDate = null
        )
    }

}