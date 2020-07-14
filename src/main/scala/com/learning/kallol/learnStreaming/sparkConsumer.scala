package com.learning.kallol.learnStreaming

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import org.apache.avro._
import org.apache.spark.sql.avro.functions._

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;



//case class UserData(User: String, Name: String)

object sparkConsumer extends App{
  
  val TOPIC=args(0)
  val spark = SparkSession
			.builder
			.appName("sparkConsumer")
			.getOrCreate()
	
	println(s"spark session created...for topic $TOPIC") 
			import spark.implicits._
			
			val df = spark
			         .readStream
			         .format("kafka")
			         .option("kafka.bootstrap.servers", "localhost:9092")
			         .option("subscribe", TOPIC)
			         .load()
     
			println("Data frame created...")         
			val user_schema = //new String(Files.readAllBytes(Paths.get("./src/main/resources/transaction.avsc")))
  
			"""
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
			println("user_schema defined")
			
      val df2 = df.select(from_avro('value, user_schema) as 'user2)
  
			//val df2 = df.selectExpr("CAST(value AS STRING)").as[String]
       
      println("df2 created ... ")
 /*     
 	  val user_df = df2.map(row=> row.split(","))
  	                   .map(row=> UserData(
  	                   row(1),
  	                   row(2)
  	                   ))
   */   
      val df_name = spark.read.format("csv").option("header","true").option("inferSchema","true").load("/home/mukherjee_kallol/stn_static_data.csv")
      
      df_name.show()
      df2.printSchema()
      
      
      
      val agg_df = df2.groupBy("user2.id").sum("user2.volume")
      
      agg_df.printSchema()
      
      val join_df = agg_df.join(df_name,agg_df("id")===df_name("stn_name"),"left_outer")
      
      join_df.printSchema()
      
      val query  = join_df
                    .writeStream
                    .trigger(Trigger.ProcessingTime("5 seconds"))
                    .outputMode("update")
                    .format("console")
                    .start()
      println("query object is created too ... ")
      
      query.awaitTermination()
      //query.
}