package com.fardjad.learning.database

import org.hibernate.dialect.HSQLDialect
import org.hsqldb.jdbc.JDBCDriver

class HSQLDatabaseExtension : TestDatabaseExtension() {
    override fun start() {}
    override fun stop() {}

    override val databaseName = "test"
    override val jdbcUrl = "jdbc:hsqldb:mem:${databaseName}"
    override val username = "sa"
    override val password = ""
    override val dialect = "${HSQLDialect().javaClass.name}"
    override val driverClassName = "${JDBCDriver.driverInstance.javaClass.name}"
}
