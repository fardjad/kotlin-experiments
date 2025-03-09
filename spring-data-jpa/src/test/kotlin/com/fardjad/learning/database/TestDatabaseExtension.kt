package com.fardjad.learning.database

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.JdbcDatabaseContainer

abstract class TestDatabaseExtension() : BeforeAllCallback, AfterAllCallback {
    abstract fun start()
    abstract fun stop()

    abstract val databaseName: String
    abstract val jdbcUrl: String
    abstract val username: String
    abstract val password: String
    abstract val dialect: String
    abstract val driverClassName: String

    override fun beforeAll(context: ExtensionContext?) {
        start()

        System.setProperty("spring.datasource.url", jdbcUrl)
        System.setProperty("spring.datasource.username", username)
        System.setProperty("spring.datasource.password", password)
        System.setProperty("spring.datasource.driver-class-name", driverClassName)
        System.setProperty("spring.jpa.hibernate.ddl-auto", "create")

        logger.atInfo()
            .setMessage("Database started")
            .addKeyValue("url", jdbcUrl)
            .addKeyValue("username", username)
            .addKeyValue("password", password)
            .log()
    }

    override fun afterAll(context: ExtensionContext?) {
        stop()
    }

    protected fun JdbcDatabaseContainer<*>.withCommonOptions(): JdbcDatabaseContainer<*> = this
        .withReuse(true)
        .withEnv(
            mapOf(
                "ACCEPT_EULA" to "Y"
            )
        )
        .withTmpFs(
            mapOf(
                "/tmpfs" to "rw"
            )
        )
        .withUsername(this@TestDatabaseExtension.username)
        .withPassword(this@TestDatabaseExtension.password)
        .withDatabaseName(this@TestDatabaseExtension.databaseName)

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TestDatabaseExtension::class.java)
    }
}