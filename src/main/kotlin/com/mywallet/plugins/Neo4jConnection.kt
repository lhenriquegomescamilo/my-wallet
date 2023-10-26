package com.mywallet.plugins

import com.mywallet.DatabaseConnection
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session

class Neo4jConnection(config: DatabaseConnectionConfig) : DatabaseConnection<Session> {
    private var driver: Driver

    init {
        val uri = config.uri
        val user = config.user
        val password = config.password
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))

    }

    override val session: Session = driver.session()


    fun close() {
        session.close()
        driver.close()
    }
}