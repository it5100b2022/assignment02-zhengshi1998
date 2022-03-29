import org.apache.kafka.streams.scala.serialization.Serdes
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._
import org.apache.kafka.streams.{KeyValue, StreamsConfig, TopologyTestDriver}
import scala.jdk.CollectionConverters._

class TestMovingAverage extends AnyFunSuite {
  import Serdes.stringSerde
  import MovingAverage.makeTopology

  test("The moving average solution is correct") {
    val props = new java.util.Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

    val testDriver = new TopologyTestDriver(makeTopology(4, "numbers", "averages"), props)

    val testInputTopic = testDriver.createInputTopic("numbers", stringSerde.serializer(), stringSerde.serializer())
    val testOutputTopic = testDriver.createOutputTopic("averages", stringSerde.deserializer(), stringSerde.deserializer())
    Option(testDriver.getKeyValueStore[String, List[String]]("store")).map(_.delete("0"))
    testInputTopic.pipeKeyValueList((1 to 10).map(i => new KeyValue("0", i.toString)).toList.asJava)
    testOutputTopic.readValuesToList().asScala shouldBe
      (2 to 8).map { i =>
        s"key = 0, " +
          s"numbers to average = [${((i + 2) until (i - 2) by -1).map(_.toDouble).mkString(",")}], " +
          s"average = ${i + 0.5}"
      }.toList
  }
}