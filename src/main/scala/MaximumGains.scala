import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, StreamsBuilder}
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer, StringSerializer}
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord}
import org.apache.kafka.streams.processor.StateStore
import org.apache.kafka.streams.scala.kstream.Materialized
import org.apache.kafka.streams.state.Stores

import java.nio.charset.StandardCharsets
import java.util.Properties
import java.time.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MaximumGains extends App {
  import Serdes._
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "moving-average1")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(AUTO_OFFSET_RESET_CONFIG, "earliest")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, stringSerde.getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, stringSerde.getClass)
  props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put("acks", "all")
  props.put("linger.ms", 1)
  props.put("retries", 0)

  implicit object PairSerde extends Serde[(Double, Double)] {
    override def serializer: Serializer[(Double, Double)] = ???
    override def deserializer: Deserializer[(Double, Double)] = ???
  }

  Future {
    val tickerSymbols = Array("AAPL", "AMZN", "MSFT", "GOOGL", "NFLX", "NVDA")
    val producer: Producer[String, String] = new KafkaProducer[String, String](props)
    (1 to 1000000).foreach { i =>
      import Math.sin
      Thread.sleep(100)
      val tickerValue = (1000 + 100*(1+0.001*i)*sin(i/10.0)*(1+sin(i/100.0))).toInt
      val r = new ProducerRecord("prices", tickerSymbols(i % tickerSymbols.length), s"$tickerValue")
      producer.send(r)
    }
  }

  def makeTopology(inputTopic: String, outputTopic: String) = {
    val builder = new StreamsBuilder
    ???
    builder.build()
  }

  val streams: KafkaStreams = new KafkaStreams(makeTopology("prices", "gains"), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10))
  }
}
