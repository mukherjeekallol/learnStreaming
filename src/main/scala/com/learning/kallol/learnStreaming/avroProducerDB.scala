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

import java.io.IOException
import java.sql.DriverManager
import java.io.InputStream
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.Properties



object avroProducerDB {
  def main(args: Array[String]) {
    
    println("Inside main......")

    val  props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("client.id","avro-producer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
   // props.put("schema.registry.url", schemaUrl)

    println("properties are set....")

    val producer = new KafkaProducer[String, Array[Byte]](props)
   
    println("Producer object is created....")
    
    val TOPIC="avro-topic"
    
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
   
   val schema_f = scala.io.Source.fromFile("avro_format.txt").mkString
   
   val schema_1: Schema = new Parser().parse(schema_f)

   println(s"user schema defined")
   
   println("Startig DB Connection module.....")
 
  val driver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://localhost/mysql"
  val username = "kalloldev"
  val password = "test123test"
  
  var connection:Connection = null
  var connection1:Connection = null
  
  Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      connection1 = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val statement1 = connection1.createStatement()
      
      println("connection successful .. ")
      
      var max_p_key = 0
      
      while (true) {
      
      var  query = "select p_key, user_id, user_name from scalatest.streaming_users where p_key >"  + max_p_key + " order by p_key"
      
      println(s"query... = ${query}") 
      val resultSet = statement.executeQuery(query)
      
      
      
      println("Query successful .. going to serialization module....")
   
   while (resultSet.next()) {
   var genericUser: GenericRecord = new GenericData.Record(schema)
   genericUser.put("id", resultSet.getString("user_id"))
   genericUser.put("name", resultSet.getString("user_name"))
   
   max_p_key = resultSet.getInt("p_key")
   
   println(s"record created for user:${resultSet.getString("user_id")}")
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
  
   println(s"producer record sent for :${resultSet.getString("user_id")}")
   
  

  }
   println("producer record sent for now....waiting for new record.. ")
  
      }
  }
}