package com.learning.kallol.learnStreaming

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.IntegerSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.common.errors.SerializationException
import java.util.Properties
import scala.util.Try
import java.io.FileReader
import java.util.Properties
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.producer._
import java.util.Collections
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser

object confAvroProducer extends App{
  
  val configFileName = args(0)
  val topicName = args(1)
  val stn_name = args(2)
  
  val props = buildProperties(configFileName)
  println("properties are built..")
  
  createTopic(topicName, 4, 3, props)
  println("topics are created...")
  
  val producer = new KafkaProducer[Integer, Payment](props)
  println("producer object is created")
  
  var partition_col = 0
   if (stn_name == "nyc") {
     partition_col = 11  // 11%4 = 3
   } 
   else if (stn_name == "pit") {
     partition_col = 12 // 12%4 = 0
   } 
   else if (stn_name == "ccu") {
     partition_col = 13 //13%4 = 1
   } 
   else  {              //14%4 = 2
     partition_col = 14   
   } 
   
  val random = new scala.util.Random
  var amount_v = random.nextDouble()*1000
  
  while(true) {
    //val countRecord: RecordJSON = new RecordJSON(i)
    //val key: String = "ID" + toString(i)
    val value : Payment = new Payment(stn_name,amount_v)
    val record = new ProducerRecord[Integer, Payment](topicName, partition_col, value)
    
    producer.send(record, callback) 
    
    println(s"producer record sent...to topic $topicName")
   
   Thread.sleep(3000L)
  }
  producer.flush()
  producer.close()
  //println("Wrote ten records to " + topicName)
  
  
  val callback = new Callback {
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
      Option(exception) match {
        case Some(err) => println(s"Failed to produce: $err")
        case None =>  println(s"Produced record at $metadata")
      }
    }
  }
  
  
   
  def buildProperties(configFileName: String): Properties = {
    val properties: Properties = new Properties
    properties.load(new FileReader(configFileName))
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer")
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer")
    properties
  }
  
  def createTopic(topic: String, partitions: Int, replication: Int, cloudConfig: Properties): Unit = {
    val newTopic = new NewTopic(topic, partitions, replication.toShort)
    val adminClient = AdminClient.create(cloudConfig)
    Try (adminClient.createTopics(Collections.singletonList(newTopic)).all.get).recover {
      case e :Exception =>
        // Ignore if TopicExistsException, which may be valid if topic exists
        if (!e.getCause.isInstanceOf[TopicExistsException]) throw new RuntimeException(e)
    }
    adminClient.close()
  }
 
  
}