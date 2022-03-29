import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, StreamsBuilder}
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer, StringSerializer}
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord}
import org.apache.kafka.streams.scala.kstream.Materialized
import org.apache.kafka.streams.state.Stores

import java.nio.charset.StandardCharsets
import java.util.Properties
import java.time.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MovingAverage extends App {
  import Serdes._
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "moving-average")
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

  implicit object ListSerde extends Serde[List[Double]] {
    override def serializer: Serializer[List[Double]] = ???
    override def deserializer: Deserializer[List[Double]] = ???
  }

  Future {
    val producer: Producer[String, String] = new KafkaProducer[String, String](props)
    (1 to 1000000).foreach { i =>
      Thread.sleep(1000)
      producer.send(new ProducerRecord[String, String]("numbers", (i % 2).toString, s"${i.toDouble}"))
    }
  }

  def makeTopology(k: Int, inputTopic: String, outputTopic: String) = {
    implicit val materializer: Materialized[String, List[Double], ByteArrayKeyValueStore] =
      Materialized.as[String, List[Double]](Stores.inMemoryKeyValueStore("price-store"))

    val builder = new StreamsBuilder
    ???
    builder.build()
  }

  val streams: KafkaStreams = new KafkaStreams(makeTopology(4, "numbers", "averages"), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10))
  }
}
