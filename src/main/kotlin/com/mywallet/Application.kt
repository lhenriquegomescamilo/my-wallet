package com.mywallet

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.GraphQLConfiguration
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import com.mywallet.application.category.usecases.CreateCategoryUseCase
import com.mywallet.infrastructure.category.gateways.CategoryGateway
import com.mywallet.infrastructure.category.gateways.CategoryValidation
import com.mywallet.infrastructure.category.graphql.CategoryMutation
import com.mywallet.infrastructure.category.graphql.CategoryQuery
import com.mywallet.plugins.DatabaseConnectionConfig
import com.mywallet.plugins.Neo4jConnection
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.routing.routing
import org.slf4j.event.Level
import sun.misc.Signal


fun main() {
    val connectionConfig = object : DatabaseConnectionConfig {
        private val config = HoconApplicationConfig(ConfigFactory.load())
        override val uri: String
            get() = config.property("ktor.database.neo4j.uri").getString()
        override val user: String
            get() = config.property("ktor.database.neo4j.user").getString()
        override val password: String
            get() = config.property("ktor.database.neo4j.password").getString()

    }
    val neo4jConnection = Neo4jConnection(connectionConfig)

    Signal.handle(Signal("INT")) {
        println("Received SIGINT, performing cleanup")
        neo4jConnection.close()
    }
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = {
        myWalletModule {
            packages = listOf("com.mywallet")
            queries = listOf(CategoryQuery())
            mutations = listOf(
                CategoryMutation(
                    CreateCategoryUseCase(
                        CategoryGateway(neo4jConnection),
                        CategoryValidation()
                    )
                )
            )
        }
    })
        .start(wait = true)

}

interface DatabaseConnection<T> {
    val session: T
}

fun Application.myWalletModule(
    schema: GraphQLConfiguration.SchemaConfiguration.() -> Unit
) {
    install(CallLogging) {
        level = Level.DEBUG
    }
    install(GraphQL) {
        schema(schema)
    }
    routing {
        graphQLPostRoute()
        graphiQLRoute()
        graphQLSDLRoute()

    }
}
