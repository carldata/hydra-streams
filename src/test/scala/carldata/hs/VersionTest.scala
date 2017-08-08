package carldata.hs

import java.time.LocalDateTime

import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.hs.EventBus.EventBusRecordJsonProtocol._
import carldata.hs.EventBus.{BatchCalculationStarted, EventBusRecord}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeRecord}
import org.scalatest.{FlatSpec, Matchers}
import spray.json.JsonParser

/**
  * Test that records can read each version of the protocol
  */
class VersionTest extends FlatSpec with Matchers {

  "DataRecord" should "parse json version 1" in {
    val source =
      """
        |{"channel": "A", "timestamp": "2015-01-01T00:00:00", "value": 100}
      """.stripMargin
    val rec = JsonParser(source).convertTo[DataRecord]
    rec shouldBe DataRecord("A", LocalDateTime.of(2015, 1, 1, 0, 0, 0), 100)
  }

  "RealTimeRecord" should "parse json version 1" in {
    val source =
      """
        |{"action": "AddAction", "calculation": "C", "script": "S", "trigger" : "T", "outputChannel" : "oC"}
      """.stripMargin
    val rec = JsonParser(source).convertTo[RealTimeRecord]
    rec shouldBe RealTimeRecord(AddAction, "C", "S", "T", "oC")
  }

  "EventBusRecord" should "parse json version 1" in {
    val source =
      """
        |{"calculationId": "C", "status": "BatchCalculationStarted" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[EventBusRecord]
    rec shouldBe EventBusRecord("C", BatchCalculationStarted)
  }
}