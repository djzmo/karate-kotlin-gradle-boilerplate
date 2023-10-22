package org.example.test.kafka

import com.intuit.karate.FileUtils
import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DatumReader
import org.apache.avro.io.DatumWriter
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets

object AvroUtils {
    fun toSchema(fileName: String): Schema {
        val parser: Schema.Parser = Schema.Parser()
        val schemaText: String = FileUtils.toString(File(fileName))
        return parser.parse(schemaText)
    }

    fun toJson(gr: GenericRecord): String {
        try {
            ByteArrayOutputStream().use { outputStream ->
                val writer: DatumWriter<GenericRecord> = GenericDatumWriter(gr.schema)
                val encoder = EncoderFactory.get().jsonEncoder(gr.schema, outputStream)
                writer.write(gr, encoder)
                encoder.flush()
                return String(outputStream.toByteArray(), StandardCharsets.UTF_8)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun fromJson(schema: Schema, json: String): GenericRecord {
        return try {
            val reader: DatumReader<Any> = GenericDatumReader(schema)
            reader.read(null, DecoderFactory.get().jsonDecoder(schema, json)) as GenericRecord
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}