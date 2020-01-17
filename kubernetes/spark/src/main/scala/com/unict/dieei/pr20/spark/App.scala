package com.unict.dieei.pr20.spark

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import scalaj.http.Http

object App {

  def main(args : Array[String]) {
    // Create context with BATCH_SIZE second batch interval
    val conf = new SparkConf().setAppName("spark-kafka")
    val batchSize = sys.env("BATCH_SIZE").toLong
    val lastBatches = sys.env("LAST_BATCHES").toInt
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

    /* Configure Telegram BOT */
    val token = sys.env("BOT_TOKEN")
    val chat = sys.env("CHAT_ID")
    val url = "https://api.telegram.org/bot" + token + "/sendMessage"

    val topics = Array(sys.env("KAFKA_MAIN_TOPIC"))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent, //Location strategy
      Subscribe[String, String](topics, kafkaParams)
    )

    var overallMeanTime:Long = 0
    var componentName:String = ""
    var componentResponseTime:Long = 0
    var lastsOverallMeanTime:Long = 0

    val logs = stream.map(_.value)

    val overallMeanTimeStream = logs
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
      .map(a => a._1 / a._2)

    overallMeanTimeStream.foreachRDD { rdd =>
      if(!rdd.isEmpty()) {
        overallMeanTime = rdd.collect()(0)
      }
    }

    val slowestComponentStream = logs
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
      .reduce((a, b) => if (a._2._1 > b._2._1) a else b)
      // Calculate mean
      .map(a => (a._1, a._2._1 / a._2._2))

    slowestComponentStream.foreachRDD { rdd =>
      if(!rdd.isEmpty()) {
        val componentInfo = rdd.collect()(0)
        componentName = componentInfo._1
        componentResponseTime = componentInfo._2
      }
    }

    val lastsOverallMeanTimeStream = overallMeanTimeStream
      // Get window of LAST_BATCHES
      .window(Seconds(lastBatches * batchSize), Seconds(batchSize))
      // Calculate mean
      .reduce((a, b) => a + b)
      .map(a => a / lastBatches)

    lastsOverallMeanTimeStream.foreachRDD { rdd =>
      if(!rdd.isEmpty()) {
        lastsOverallMeanTime = rdd.collect()(0)
        if(overallMeanTime > 0) {
          if(overallMeanTime > 1.2 * lastsOverallMeanTime) {
            // Send message to Telegram BOT
            val increment = (overallMeanTime.toFloat / lastsOverallMeanTime - 1) * 100
            val alertMessage = "Registrato un incremento del tempo medio di risposta pari a " + increment + "%. " +
              "Il componente che mediamente ha il maggior tempo di risposta e' " + componentName + ", con un " +
              "tempo medio di " + componentResponseTime
            Http(url).param("chat_id", chat).param("text", alertMessage).asString
          }
          val logMessage = "Overall Mean Time:\n" + overallMeanTime + "\nComponent Name:\n" + componentName +
            "\nComponent Response Time:\n" + componentResponseTime + "\nLasts Overall Mean Time:\n" + lastsOverallMeanTime
          Http(url).param("chat_id", chat).param("text", logMessage).asString

          overallMeanTime = 0
          componentName = ""
          componentResponseTime = 0
          lastsOverallMeanTime = 0
        }
      }
    }

    //windowedOverallMeanTime.print()
    //slowestComponent.print()

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
}
