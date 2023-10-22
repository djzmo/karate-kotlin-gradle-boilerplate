package org.example.test

import com.intuit.karate.Runner
import org.example.Application
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext

@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class IntegrationTest(
    private val webContext: ServletWebServerApplicationContext
) {
    @Test
    fun testFeature() {
        val results = Runner.path("classpath:org/example/karate")
            .systemProperty("server.port", webContext.webServer.port.toString())
            .parallel(5)
        assertEquals(0, results.failCount, results.errorMessages)
    }
}
