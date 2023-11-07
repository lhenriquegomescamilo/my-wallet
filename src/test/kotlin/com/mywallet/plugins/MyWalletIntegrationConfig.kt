package com.mywallet.plugins

import com.mywallet.application.category.usecases.CreateCategoryUseCase
import com.mywallet.infrastructure.category.gateways.CategoryRepositoryGateway
import com.mywallet.infrastructure.category.graphql.CategoryMutation
import com.mywallet.infrastructure.category.graphql.CategoryQuery
import com.mywallet.myWalletModule
import io.ktor.server.application.Application
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import org.junit.After
import org.neo4j.driver.Query
import org.testcontainers.containers.Neo4jContainer

abstract class MyWalletIntegrationConfig {
    private lateinit var neo4jContainer: Neo4jContainer<Nothing>

    protected lateinit var connectionConfig: DatabaseConnectionConfig

    @BeforeTest
    fun beforeSetup() {
        neo4jContainer = Neo4jContainer<Nothing>("neo4j:community-ubi8").apply {
            withAdminPassword("test_neo4j_password")
        }
        neo4jContainer.start()

        connectionConfig = object : DatabaseConnectionConfig {
            override val uri: String
                get() = neo4jContainer.boltUrl
            override val user: String
                get() = "neo4j"
            override val password: String
                get() = neo4jContainer.adminPassword

        }
    }


    @After
    fun afterEach() {
        Neo4jConnection(connectionConfig).session.executeWriteWithoutResult { trx ->
            trx.run(Query("match (a) -[r]-> () delete a, r"))
            trx.run(Query("match (a) delete a"))
        }
    }

    @AfterTest
    fun afterTest() {
        neo4jContainer.stop()
    }

}