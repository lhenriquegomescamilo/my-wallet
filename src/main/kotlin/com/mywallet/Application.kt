package com.mywallet

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import com.expediagroup.graphql.server.operations.Query
import com.mywallet.plugins.Neo4jConnection
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(GraphQL) {
        schema {
            packages = listOf("com.mywallet")
            queries = listOf(
                WalletQuery()
            )
        }
    }
    routing {
        graphQLPostRoute()
        graphiQLRoute()
        graphQLSDLRoute()
    }
}

class WalletQuery(
    config: HoconApplicationConfig = HoconApplicationConfig(ConfigFactory.load())
) : Query {
    private var connection: Neo4jConnection = Neo4jConnection(
        config.property("ktor.database.neo4j.uri").getString(),
        config.property("ktor.database.neo4j.user").getString(),
        config.property("ktor.database.neo4j.password").getString(),
    )

    fun hello() = "Hello world"

    fun findAll(): List<Player> {
        val result = connection.session.run("MATCH(n:PLAYER) RETURN n.name AS name")
        val players: MutableList<Player> = result.list { Player(it.get("name").asString()) }
        return players
    }

}

data class Player(val name: String) {}
