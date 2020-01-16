package com.unict.dieei.pr20.spark

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object App {

  def main(args : Array[String]) {
    // Create context with BATCH_SIZE second batch interval
    val conf = new SparkConf().setAppName("spark-kafka")
    val batchSize = sys.env("BATCH_SIZE").toLong
    val ssc = new StreamingContext(conf, Seconds(batchSize))

    /* Configure Kafka */
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> sys.env("KAFKA_ADDRESS"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> sys.env("KAFKA_GROUP_ID"),
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array(sys.env("KAFKA_MAIN_TOPIC"))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent, //Location strategy
      Subscribe[String, String](topics, kafkaParams)
    )

    val logs = stream.map(_.value)

    val overallMeanTime = logs
      // Get response times from requests
      .map(log => {
        val fields = log.split("\\|")
        val requestId = fields(5).toLong
        val responseTime = fields(6).toLong
        (requestId, responseTime)
      })
      // Calculate overall response times
      .reduceByKey(_ + _)
      // Calculate mean
      .map(a => (a._2, 1))
      .reduce((a, b) => (a._1 + b._1, a._2 + b._2))
      .map(a => a._1/a._2)

    val slowestComponent = logs
      // Get response times per component
      .map(log => {
        val fields = log.split("\\|")
        val component = fields(2)
        val responseTime = fields(6).toLong
        (component, responseTime)
      })
      // Calculate overall response times per component
      .map(a => (a._1, (a._2, 1)))
      .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))
      // Get component with maximum response time
      .reduce((a, b) => if(a._2._1 > b._2._1) a else b)
      // Calculate mean
      .map(a => (a._1, a._2._1/a._2._2))

    overallMeanTime.print()

    slowestComponent.print()

/*
    overallMeanTime.foreachRDD { rdd =>
      rdd.foreach(println)
    }

    slowestComponent.foreachRDD { rdd =>
      rdd.foreach(println)
    }
*/
    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }

}
