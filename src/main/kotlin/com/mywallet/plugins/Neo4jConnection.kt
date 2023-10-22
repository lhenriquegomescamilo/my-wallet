package com.mywallet.plugins

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session

class Neo4jConnection(uri: String, user: String, password: String)  {
    private val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
    val session: Session = driver.session()

    fun close() {
        session.close()
        driver.close()
    }
}