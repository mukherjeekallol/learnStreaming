package com.learning.kallol.learnStreaming

import java.util.Properties
import org.apache.kafka.clients.producer._
import java.util.{Properties, UUID}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer._
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.io._
import java.io.ByteArrayOutputStream
import scala.io.Source
import java.util.concurrent.TimeUnit
import java.nio.ByteBuffer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization._

import org.apache.kafka.common.errors.SerializationException
import scala.collection.JavaConverters._
import java.util.Collections.singletonList
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import java.util
import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.collection.JavaConverters._
import java.util.Properties
import scala.collection.JavaConversions._

object avroConsumer extends App{
  
  val TOPIC=args(0)

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("client.id","avro-consumer")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
  props.put("group.id", "something")

  val consumer = new KafkaConsumer[String, Array[Byte]](props)

  consumer.subscribe(util.Collections.singletonList(TOPIC))
  
  println(s"subscribed to topic... $TOPIC")
  
  val user_schema = """
      {
    "namespace": "kakfa-avro.test",
     "type": "record",
     "name": "user2",
     "fields":[
         {  "name": "id", "type": "string"},
         {"name": "amount", "type": "double"},
         {"name": "volume",  "type": "int"}
     ]
      } 
      
      """
    
    
   val schema: Schema = new Parser().parse(user_schema)

   println(s"user schema defined")

   println("before poll......")
  while(true){
    val records=consumer.poll(1000).asScala
    
   // println(s"poll was successful..... ${records}.... ${records.asScala}")
   // println(records.iterator())
    
   // records.forEach({
   //   record => get_data(record.value())
           
   // })
    
    for (record<-records.iterator){
     println("within reading loop...kallol") 
     println(record.key(),record.value())
     get_data(record.value())
    }
  }
     println("loop completed")
   
     
     def get_data(message: Array[Byte]) = {
 // Deserialize and create generic record
     val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](schema)
     val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)
     val userData: GenericRecord = reader.read(null, decoder)
     
     println("within the function........")
     println(userData.get("id").toString)
     println(userData.get("amount").toString)
     println(userData.get("volume").toString)
   
  }
  
}