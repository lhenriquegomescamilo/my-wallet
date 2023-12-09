package com.mywallet.infrastructure.expense.gatways

import com.mywallet.DatabaseConnection
import com.mywallet.application.expense.gateways.CreateExpenseGateway
import com.mywallet.domain.entity.Expense
import org.neo4j.cypherdsl.core.Cypher
import org.neo4j.driver.Query
import org.neo4j.driver.Session
import java.math.BigDecimal

class CreateExpenseGatewayRepository(private val connection: DatabaseConnection<Session>) : CreateExpenseGateway {
    override suspend fun create(expense: Expense): Expense {
        return connection.session.executeWrite { transaction ->
            /**
            MATCH (mirna: Owner {publicId: "ba82cbcf-334f-4976-99d2-5f370829b316"})
            MATCH (supermarket: Category {publicId: "c3acde06-e767-41c2-9a2d-e23d826b5e61"})
            MERGE (mirna)-[EXPENDED_IN:EXPENDED_IN { price: 1000.12}]->(supermarket)


            Node sourceNode = Cypher.node("Label1").named("source");
            Node targetNode = Cypher.node("Label2").named("target");

            // Define the relationship type and symbolic name
            Relationship relationship = Cypher.relationshipBetween(sourceNode, targetNode).named("relType");

            // Define properties for the relationship
            relationship = relationship.withProperties(Cypher.mapOf("propertyKey", Cypher.literalOf("propertyValue")));

            // Build the MERGE query to create or match the relationship
            Statement statement = Cypher.merge(sourceNode.relationshipTo(targetNode, "relType").withProperties(Cypher.mapOf("propertyKey", Cypher.literalOf("propertyValue")))).build();

            // Print the Cypher query
            System.out.println(statement.getCypher());
             */

            val cypherQuery = """
                    MATCH (owner: Owner {publicId: ":ownerPublicId" })
                    MATCH (category: Category {publicId: ":categoryPublicId" })
                    MERGE (owner)-[EXPENDED_IN:EXPENDED_IN { price: :price}]->(category)
                """

            val owner = Cypher.node("Owner")
                .named("owner")

            val category = Cypher.node("Category").named("category")

            val expendedIn = owner.relationshipTo(category, "EXPENDED_IN")
                .named("expendedIn")
                .withProperties("price", Cypher.literalOf<BigDecimal>(expense.price.value))

            val statement = Cypher.match(owner)
                .match(category)
                .merge(expendedIn)
                .returning(expendedIn)
                .build()


            val query = Query(
                statement.cypher,
                mapOf(
                    "ownerPublicId" to expense.owner.publicId,
                    "categoryPublicId" to expense.category.publicId,
                    "price" to expense.price,
                )
            )
            transaction.run(query)
        }.let { expense }




        return expense
    }

    override suspend fun checkIfExists(input: Expense): Boolean {
        TODO("Not yet implemented")
    }
}