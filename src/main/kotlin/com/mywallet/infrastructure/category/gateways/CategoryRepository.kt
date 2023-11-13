package com.mywallet.infrastructure.category.gateways

import com.mywallet.DatabaseConnection
import com.mywallet.application.category.gateways.CategoryRepositoryGateway
import com.mywallet.domain.entity.Category
import org.neo4j.cypherdsl.core.Cypher
import org.neo4j.cypherdsl.core.Functions
import org.neo4j.driver.Query
import org.neo4j.driver.Session

data class CategoryModel(val publicId: String, val name: String) {
    fun toDomain(): Category {
        return Category(
            publicId = publicId,
            name = name
        )
    }
}


class CategoryGateway(private val connection: DatabaseConnection<Session>) : CategoryRepositoryGateway {
    override suspend fun create(category: Category): Category {
        val categoryModel = connection.session.executeWrite { transaction ->

            val categoryNode = Cypher.node("Category").named("category")
            val statement = Cypher.create(categoryNode)
                .set(categoryNode.property("publicId").to(Cypher.parameter("publicId")))
                .set(categoryNode.property("name").to(Cypher.parameter("name")))
                .returning(categoryNode)
                .build()


            val query = Query(
                statement.cypher,
                mapOf(
                    "publicId" to category.publicId,
                    "name" to category.name
                )
            )
            val result = transaction.run(query)
            val node = result.single().get(0)
            CategoryModel(
                name = node.get("name").asString(),
                publicId = node.get("publicId").asString()
            )
        }
        return categoryModel.toDomain()
    }

    override suspend fun checkIfExists(category: Category): Boolean = connection.session.executeRead { transaction ->
        val categoryNode = Cypher.node("Category").named("category")
        val statement = Cypher.match(categoryNode)
            .where(Functions.toLower(categoryNode.property("name")).isEqualTo(Cypher.parameter("name")))
            .with(Functions.count(categoryNode).gt(Cypher.parameter("min_categories")).`as`("category_exists"))
            .returning(Cypher.name("category_exists"))
            .build()


        val query = Query(
            statement.cypher,
            mapOf(
                "name" to category.name.lowercase(),
                "min_categories" to 0,
            )
        )
        val result = transaction.run(query)
        result.single().get(0).asBoolean()
    }
}