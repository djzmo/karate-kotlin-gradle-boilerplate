package org.example.test.utils

import com.intuit.karate.Logger
import com.intuit.karate.core.ScenarioEngine
import org.example.test.kafka.KarateKafkaConsumer
import org.example.test.kafka.KarateKafkaProducer

class KafkaUtils(private val topic: String) {
    private val consumer: KarateKafkaConsumer = KarateKafkaConsumer(topic)
    private val producer: KarateKafkaProducer = KarateKafkaProducer(topic, "src/test/kotlin/org/example/schema/test.avsc")

    fun send(data: Any) {
        logger().debug(">> kafka send [{}]", topic)
        producer.send(data)
    }

    fun listen(): List<*> {
        return consumer.getMessages()
    }

    companion object {
        private fun logger(): Logger {
            val engine = ScenarioEngine.get()
            return engine.logger
        }
    }
}
