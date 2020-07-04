package com.learning.kallol.learnStreaming

import java.util
import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.collection.JavaConverters._
import java.util.Properties
import scala.collection.JavaConversions._

// This simple Scala consumer will consume record from a Kafka-topic and write to a console.
// Uses simple String DeSerializer.

object simpleConsumer extends App{
    
  val TOPIC="kafka-topic"

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("client.id","simple-consumer")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")

  //properties are set  
  val consumer = new KafkaConsumer[String, String](props)
  
  consumer.subscribe(util.Collections.singletonList(TOPIC))
  //consumer object is created andsubscribed for the topic
  
  //println("starting to read consumer..")

  while(true){
    val records=consumer.poll(100)
    for (record<-records.asScala){
     println("within reading loop...kallol") 
     println(record.key(),record.value())
    }
  }
}