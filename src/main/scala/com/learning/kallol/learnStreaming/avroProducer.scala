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
    
    val TOPIC=args(0)
 //defining the avro schema   
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
//parse the schema
   println(s"user schema defined")
 
  // create a generic user record
   val genericUser: GenericRecord = new GenericData.Record(schema)
  // assign values to the generic record using loop 
   
   val random = new scala.util.Random
   val stn_name = args(1)
   
   println(s"sending producer record for station : $stn_name")
   
   while(true) { 
   genericUser.put("id", s"$stn_name")
   genericUser.put("amount", random.nextDouble()*1000)
   genericUser.put("volume", random.nextInt(100)*10)
   
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
   
   var partition_col = 0
   if (stn_name == "nyc") {
     partition_col = 100
   } 
   else if (stn_name == "bom") {
     partition_col = 200
   } 
   else if (stn_name == "nyc") {
     partition_col = 300
   } 
   else if (stn_name == "nyc") {
     partition_col = 400
   } 
   else if (stn_name == "nyc") {
     partition_col = 500
   } 
   else  {
     partition_col = 1000
   } 
   
   
   producer.send(new ProducerRecord[String, Array[Byte]](TOPIC, stn_name,serializedBytes))
  
   println(s"producer record sent...to topic $TOPIC")
   
   Thread.sleep(3000L)
  
   }
  
}