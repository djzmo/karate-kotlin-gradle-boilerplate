package org.example.test.kafka

import com.intuit.karate.Json
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class KarateKafkaConsumer(topic: String) {
    private val kafka: KafkaConsumer<String, GenericRecord> = KafkaConsumer(config())
    private val partitionFuture: CompletableFuture<Boolean?> = CompletableFuture()
    private val listenFuture: CompletableFuture<Boolean?> = CompletableFuture()
    private val executor = Executors.newSingleThreadExecutor()
    private val messages: MutableList<*> = ArrayList<Any?>()

    init {
        listen(topic) // will block until partition is ready
    }

    private fun listen(topic: String) {
        kafka.subscribe(listOf(topic))
        logger.debug("kafka consumer subscibed to topic: {}", topic)
        executor.submit {
            while (true) {
                val records: ConsumerRecords<String, GenericRecord> = kafka.poll(Duration.ofMillis(1000))
                if (!records.isEmpty()) {
                    for (record in records) {
                        logger.debug(
                            "<< kafka offet: {}, key: {}, value: {}",
                            record.offset(),
                            record.key(),
                            record.value()
                        )
                        val json: String = AvroUtils.toJson(record.value())
                        messages.add(Json.of(json).value())
                    }
                    listenFuture.complete(true)
                    break
                }
                // assignment() can only be called after poll() has been called at least once
                val partitions: Set<TopicPartition> = kafka.assignment()
                if (partitions.isEmpty()) {
                    try {
                        logger.debug("waiting for partition ...")
                        Thread.sleep(1000)
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                } else {
                    logger.debug("partitions: {}", partitions)
                    partitionFuture.complete(true)
                }
            }
        }
        try {
            partitionFuture.get()
            logger.debug("kafka consumer partition ready")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getMessages(): List<*> {
        try {
            logger.debug("kafka consumer waiting for messages ...")
            listenFuture.get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return messages
    }

    fun close() {
        kafka.close()
        executor.shutdown()
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(KarateKafkaConsumer::class.java)
        fun config(): Properties {
            // refer https://kafka.apache.org/documentation/#consumerconfigs
            val props = Properties()
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092")
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java)
            props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
            // we use a random id to avoid keeoing track and having to seek to the beginning
            props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString())
            return props
        }
    }
}