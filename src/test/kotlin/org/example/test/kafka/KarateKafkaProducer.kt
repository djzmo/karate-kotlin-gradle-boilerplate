package org.example.test.kafka

import com.intuit.karate.Json
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.example.test.kafka.AvroUtils.fromJson
import org.example.test.kafka.AvroUtils.toSchema
import java.util.*

class KarateKafkaProducer(
    private val topic: String,
    private val schemaFileName: String
) {
    private val kafka: KafkaProducer<String, GenericRecord> = KafkaProducer<String, GenericRecord>(config())
    private val helloSchema: Schema = toSchema(schemaFileName)

    fun send(data: Any?) {
        val json: String = Json.of(data).toString()
        val record = fromJson(helloSchema, json)
        val pr = ProducerRecord<String, GenericRecord>(topic, null, record)
        kafka.send(pr)
    }

    fun close() {
        kafka.close()
    }

    companion object {
        fun config(): Properties {
            // refer https://kafka.apache.org/documentation/#producerconfigs
            val props = Properties()
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092")
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java)
            props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")

            // create a safe producer
            props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
            props.put(ProducerConfig.ACKS_CONFIG, "all")
            props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5")
            // high throughput producer
            props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy")
            props.put(ProducerConfig.LINGER_MS_CONFIG, "20") // linger for 20ms
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024)) // 32 kb
            return props
        }
    }
}