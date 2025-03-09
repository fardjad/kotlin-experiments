package com.fardjad.learning.database

import org.hibernate.dialect.PostgreSQLDialect
import org.postgresql.Driver
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer

class PostgreSQLDatabaseExtension : TestDatabaseExtension() {
    private var container: JdbcDatabaseContainer<*>? = null

    override fun start() {
        if (container != null) {
            return
        }

        container = PostgreSQLContainer("postgres:17")
            .withCommonOptions()
            .apply {
                portBindings = listOf("5432:5432")
                start()
            }
    }

    override fun stop() {
        container?.stop()
    }

    override val databaseName = "test"
    override val jdbcUrl: String
        get() = container?.jdbcUrl ?: throw IllegalStateException("Container not created yet")
    override val username = "postgres"
    override val password = "password"
    override val dialect = "${PostgreSQLDialect().javaClass.name}"
    override val driverClassName = "${Driver::class.java.name}"
}
