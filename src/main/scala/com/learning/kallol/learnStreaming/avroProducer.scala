package com.learning.kallol.learnStreaming

import java.util.Properties
import org.apache.kafka.clients.producer._
import java.util.{Properties, UUID}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.avro.specific.SpecificDatumWriter

import org.apache.avro.io._
import java.io.ByteArrayOutputStream
import scala.io.Source
import java.util.concurrent.TimeUnit
import java.nio.ByteBuffer
import org.apache.kafka.common.serialization._
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.ByteArraySerializer

import org.apache.kafka.common.errors.SerializationException

object avroProducer extends App{
  
    println("Inside main......")
// setting properties for the Producer Object
// will use Avro serializer for the values..    
    val  props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("client.id","avro-producer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
   // props.put("schema.registry.url", schemaUrl)

    println("properties are set....")

    val producer = new KafkaProducer[String, Array[Byte]](props)
 //creating a new producer with the properties  
    println("Producer object is created....")
    
    val TOPIC="avro-topic"
 //defining the avro schema   
    val user_schema = """
      {
    "namespace": "kakfa-avro.test",
     "type": "record",
     "name": "user",
     "fields":[
         {  "name": "id", "type": "string"},
         {   "name": "name",  "type": "string"}
     ]
      } 
      
      """
    
   val schema: Schema = new Parser().parse(user_schema)
//parse the schema
   println(s"user schema defined")
 
  // create a generic user record
   val genericUser: GenericRecord = new GenericData.Record(schema)
  // assign values to the generic record using loop 
   
   
    for(i<- 1 to 100) { 
   genericUser.put("id", s"user$i")
   genericUser.put("name", s"person_name$i")
   
   
   println(s"user record created")
   println(genericUser)
   
   val writer = new SpecificDatumWriter[GenericRecord](schema)
   val out = new ByteArrayOutputStream()
   val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
   writer.write(genericUser, encoder)
   encoder.flush()
   //out.close()
   
   println(s"Serialization done...")
   
   
   val serializedBytes: Array[Byte] = out.toByteArray()
 
   println(serializedBytes)
  // serializedBytes
   
   producer.send(new ProducerRecord[String, Array[Byte]](TOPIC, "ABC",serializedBytes))
  
   println(s"producer record sent...")
   
  
   }
  
}