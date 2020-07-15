package com.learning.kallol.learnStreaming

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.IntegerDeserializer

import java.time.Duration
import java.util.Collections
import java.util.Properties
import java.io.FileReader
import java.time.Duration
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


object confAvroConsumer extends App{
  
   println("consumer code starts here.... ")
  
  val configFileName = args(0)
  val topicName = args(1)
  val props = buildProperties(configFileName)
  val consumer = new KafkaConsumer[Integer, Payment](props)
  
  consumer.subscribe(Collections.singletonList(topicName))
  
  var total_count = 0L
    while(true) {
      println("Polling")
      val records = consumer.poll(1000).asScala
      println("polling done...")
      for (record <- records.iterator) {
        val key = record.key()
        println("got value of key...")
        val value = record.value()
        println("got value of value...")
   //     val countRecord = MAPPER.treeToValue(value, classOf[RecordJSON])
        total_count += 1
        println(s"Consumed record with key $key and value $value, and updated total count to $total_count")
      }
    }
    consumer.close()
  
  def buildProperties(configFileName: String): Properties = {
      val properties = new Properties()
      properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer")
      properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer")
      properties.put(ConsumerConfig.GROUP_ID_CONFIG, "scala_example_group")
      properties.put("specific.avro.reader", "true")
      properties.load(new FileReader(configFileName))
      properties
    }
  
}
  
