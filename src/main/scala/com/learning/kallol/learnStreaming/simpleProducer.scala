package com.learning.kallol.learnStreaming

import java.util.Properties
import org.apache.kafka.clients.producer._

// This is a simple scala producer code, which uses some simple StringSerializer.

object simpleProducer extends App{
  
    
    val  props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("client.id","simple-producer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    //println("properties are set....")
    
    val producer = new KafkaProducer[String, String](props)
   
    //println("Producer object is created....")
    
    val TOPIC="kafka-topic"
    var i = 0
    while (true){
    val record = new ProducerRecord(TOPIC, "key", s"hello $i")
    i+=1
    //println("sending to topic....")
    
    producer.send(record)
 }
    
    val record = new ProducerRecord(TOPIC, "key", "the end "+new java.util.Date)
    producer.send(record)

    //println("produced 50 records")
 
    producer.close()
    
    
  
}