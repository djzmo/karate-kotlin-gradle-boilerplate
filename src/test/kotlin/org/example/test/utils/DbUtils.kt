package org.example.test.utils

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource

class DbUtils(config: Map<String?, Any?>) {
    private val jdbc: JdbcTemplate

    init {
        val url = config["url"] as String?
        val username = config["username"] as String?
        val password = config["password"] as String?
        val driver = config["driverClassName"] as String?
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(driver)
        dataSource.url = url
        dataSource.username = username
        dataSource.password = password
        jdbc = JdbcTemplate(dataSource)
    }

    fun readValue(query: String): Any? {
        return jdbc.queryForObject(query, Any::class.java)
    }

    fun readRow(query: String): Map<String, Any> {
        return jdbc.queryForMap(query)
    }

    fun readRows(query: String): List<Map<String, Any>> {
        return jdbc.queryForList(query)
    }
}