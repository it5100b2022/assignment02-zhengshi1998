import org.apache.kafka.streams.scala.serialization.Serdes
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._
import org.apache.kafka.streams.{KeyValue, StreamsConfig, TopologyTestDriver}
import scala.jdk.CollectionConverters._

class TestMaximumGains extends AnyFunSuite {
  import Serdes.stringSerde
  import MaximumGains.makeTopology

  test("The moving average solution is correct") {
    val props = new java.util.Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

    val testDriver = new TopologyTestDriver(makeTopology("prices", "gains"), props)

    val testInputTopic = testDriver.createInputTopic("prices", stringSerde.serializer(), stringSerde.serializer())
    val testOutputTopic = testDriver.createOutputTopic("gains", stringSerde.deserializer(), stringSerde.deserializer())
    val tickerSymbols = Array("AAPL", "AMZN", "MSFT", "GOOGL", "NFLX", "NVDA")
    Option(testDriver.getKeyValueStore[String, String]("price-store")).foreach(store => tickerSymbols.foreach(store.delete))
    val inputVals = (1 to 44).map { i =>
      import java.lang.Math.sin
      val tick = (1000 + 100 * (1 + 0.001 * i) * sin(i / 10.0) * (1 + sin(i / 100.0))).toInt.toString
      new KeyValue(tickerSymbols(i % tickerSymbols.length), tick)
    }.toList.asJava
    testInputTopic.pipeKeyValueList(inputVals)
    testOutputTopic.readValuesToList().asScala shouldBe List(
      "asset: AMZN, current price: 1069.0, lowest previous value: 1010.0, gain: 59.0",
      "asset: MSFT, current price: 1078.0, lowest previous value: 1020.0, gain: 58.0",
      "asset: GOOGL, current price: 1086.0, lowest previous value: 1030.0, gain: 56.0",
      "asset: NFLX, current price: 1093.0, lowest previous value: 1040.0, gain: 53.0",
      "asset: NVDA, current price: 1099.0, lowest previous value: 1050.0, gain: 49.0",
      "asset: AAPL, current price: 1105.0, lowest previous value: 1060.0, gain: 45.0",
      "asset: AMZN, current price: 1110.0, lowest previous value: 1010.0, gain: 100.0",
      "asset: MSFT, current price: 1113.0, lowest previous value: 1020.0, gain: 93.0",
      "asset: GOOGL, current price: 1116.0, lowest previous value: 1030.0, gain: 86.0",
      "asset: NFLX, current price: 1117.0, lowest previous value: 1040.0, gain: 77.0",
      "asset: NVDA, current price: 1117.0, lowest previous value: 1050.0, gain: 67.0",
      "asset: AAPL, current price: 1116.0, lowest previous value: 1060.0, gain: 56.0",
      "asset: AMZN, current price: 1114.0, lowest previous value: 1010.0, gain: 104.0",
      "asset: MSFT, current price: 1111.0, lowest previous value: 1020.0, gain: 91.0",
      "asset: GOOGL, current price: 1106.0, lowest previous value: 1030.0, gain: 76.0",
      "asset: NFLX, current price: 1100.0, lowest previous value: 1040.0, gain: 60.0",
      "asset: NVDA, current price: 1093.0, lowest previous value: 1050.0, gain: 43.0",
      "asset: AAPL, current price: 1085.0, lowest previous value: 1060.0, gain: 25.0",
      "asset: AMZN, current price: 1076.0, lowest previous value: 1010.0, gain: 66.0",
      "asset: MSFT, current price: 1066.0, lowest previous value: 1020.0, gain: 46.0",
      "asset: GOOGL, current price: 1055.0, lowest previous value: 1030.0, gain: 25.0",
      "asset: NFLX, current price: 1043.0, lowest previous value: 1040.0, gain: 3.0"
    )
  }
}
