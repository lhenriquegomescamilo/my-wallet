package com.mywallet.infrastructure.owner.gateways

import com.mywallet.DatabaseConnection
import com.mywallet.application.owner.gateways.CreateOwnerRepositoryGateway
import com.mywallet.domain.entity.Owner
import com.mywallet.infrastructure.owner.model.OwnerModel
import org.neo4j.cypherdsl.core.Cypher
import org.neo4j.cypherdsl.core.Cypher.parameter
import org.neo4j.driver.Query
import org.neo4j.driver.Session
import java.util.UUID

class CreateOwnerRepository(private val connection: DatabaseConnection<Session>) : CreateOwnerRepositoryGateway {
    override suspend fun create(input: Owner): Owner = connection.session.executeWrite { transaction ->
        val node = Cypher.node("Owner").named("owner")
        val statement = Cypher.create(node)
            .set(node.property("name").to(parameter("name")))
            .set(node.property("publicId").to(parameter("publicId")))
            .returning(node)
            .build()

        val publicId = UUID.randomUUID().toString()
        val query = Query(
            statement.cypher,
            mapOf(
                "publicId" to publicId,
                "name" to input.name
            )
        )
        val outputQuery = transaction.run(query)
        val outputNode = outputQuery.single().get(0)
        OwnerModel(
            name = outputNode.get("name").asString(),
            publicId = outputNode.get("publicId").asString()
        )
    }.asDomain()
}
