package com.mywallet.infrastructure.expense.gatways

import com.mywallet.DatabaseConnection
import com.mywallet.application.expense.gateways.CreateExpenseGateway
import com.mywallet.domain.entity.Expense
import org.neo4j.cypherdsl.core.Cypher.literalOf
import org.neo4j.cypherdsl.core.Cypher.match
import org.neo4j.cypherdsl.core.Cypher.node
import org.neo4j.driver.Query
import org.neo4j.driver.Session
import java.util.*

class CreateExpenseGatewayRepository(private val connection: DatabaseConnection<Session>) : CreateExpenseGateway {
    override suspend fun create(expense: Expense): Expense = connection.session.executeWrite { transaction ->

        val owner = matchOwnerByPublicId(expense.owner.publicId)
        val category = matchCategoryByPublicId(expense.category.publicId)

        val expendedIn = owner.relationshipTo(category, "EXPENDED_IN")
            .named("expendedIn")
            .withProperties("price", literalOf<Double>(expense.price.value.toDouble()))
            .withProperties("publicId", literalOf<String>(UUID.randomUUID().toString()))

        val statement = match(owner)
            .match(category)
            .merge(expendedIn)
            .returning(expendedIn)
            .build()


        val result = transaction.run(Query(statement.cypher))
        val output = result.single().get(0)
        expense.copy(publicId = output.get("publicId").asString())
    }

    private fun matchCategoryByPublicId(publicId: String) = node("Category")
        .named("category")
        .withProperties("publicId", literalOf<String>(publicId))

    private fun matchOwnerByPublicId(publicId: String) = node("Owner")
        .named("owner")
        .withProperties("publicId", literalOf<String>(publicId))

}