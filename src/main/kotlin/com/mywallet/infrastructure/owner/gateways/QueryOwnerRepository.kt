package com.mywallet.infrastructure.owner.gateways

import com.mywallet.DatabaseConnection
import com.mywallet.application.owner.gateways.QueryOwnerGateway
import com.mywallet.domain.entity.Owner
import com.mywallet.infrastructure.owner.model.OwnerModel
import org.neo4j.cypherdsl.core.Cypher
import org.neo4j.driver.Query
import org.neo4j.driver.Session

class QueryOwnerRepository(private val connection: DatabaseConnection<Session>) : QueryOwnerGateway {
    override suspend fun findByPublicId(publicId: String): Owner = connection.session.executeRead { transaction ->
        val ownerNode = Cypher.node("Owner").named("owner")
        val statement = Cypher.match(ownerNode)
            .where(ownerNode.property("publicId").isEqualTo(Cypher.parameter("publicId")))
            .returning(ownerNode)
            .build()

        val query = Query(
            statement.cypher,
            mapOf("publicId" to publicId)
        )

        val result = transaction.run(query)
        val record = result.single().get(0)

        OwnerModel(
            name = record.get("name").asString(),
            publicId = record.get("publicId").asString(),
        )
    }.asDomain()
}