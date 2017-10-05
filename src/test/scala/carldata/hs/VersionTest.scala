package carldata.hs

import java.time.LocalDateTime

import carldata.hs.Batch.BatchRecordJsonProtocol._
import carldata.hs.Batch.BatchRecord
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.hs.EventBus.EventBusRecordJsonProtocol._
import carldata.hs.EventBus.{Started, EventBusRecord}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeJobRecord}
import org.scalatest.{FlatSpec, Matchers}
import spray.json.JsonParser

/**
  * Test that records can read each version of the protocol
  */
class VersionTest extends FlatSpec with Matchers {

  "DataRecord" should "parse json version 1" in {
    val source =
      """
        |{"channelId": "A", "timestamp": "2015-01-01T00:00:00", "value": 100}
      """.stripMargin
    val rec = JsonParser(source).convertTo[DataRecord]
    rec shouldBe DataRecord("A", LocalDateTime.of(2015, 1, 1, 0, 0, 0), 100)
  }

  it should "parse null value" in {
    val source =
      """
        |{"channelId": "A", "timestamp": "2015-01-01T00:00:00", "value": null}
      """.stripMargin
    val rec = JsonParser(source).convertTo[DataRecord]
    rec.value.isNaN shouldBe true
  }

  "RealTimeRecord" should "parse json version 1" in {
    val source =
      """
        |{
        |"action": "AddAction",
        |"calculationId": "C",
        |"script": ["line1", "line2"],
        |"inputChannelIds": ["IcId", "in2"],
        |"outputChannelId" : "oC"}
      """.stripMargin
    val rec = JsonParser(source).convertTo[RealTimeJobRecord]
    rec shouldBe RealTimeJobRecord(AddAction, "C", List("line1", "line2").mkString("\n"), List("IcId", "in2"), "oC")
  }

  "EventBusRecord" should "parse json version 1" in {
    val source =
      """
        |{"source": "hydra", "id": "123", "status": "Started", "msg": "some message" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[EventBusRecord]
    rec shouldBe EventBusRecord("hydra", "123", Started, "some message")
  }

  "BatchRecord" should "parse json version 1" in {
    val source =
      """
        |{"calculationId": "CId",
        |"script": ["line1", "line2"],
        |"inputChannelIds": ["IcId", "in2"],
        |"outputChannelId" : "OcId",
        |"startDate" : "2015-01-01T00:00:00",
        |"endDate": "2015-01-01T00:00:00" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[BatchRecord]
    rec shouldBe BatchRecord("CId", List("line1", "line2").mkString("\n"), List("IcId", "in2"), "OcId",
      LocalDateTime.of(2015, 1, 1, 0, 0, 0), LocalDateTime.of(2015, 1, 1, 0, 0, 0))
  }

  "BatchRecord" should "parse json with date only" in {
    val source =
      """
        |{"calculationId": "CId",
        |"script": ["line1", "line2"],
        |"inputChannelIds": ["IcId"],
        |"outputChannelId" : "OcId",
        |"startDate" : "2015-01-01",
        |"endDate": "2015-01-12" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[BatchRecord]
    rec shouldBe BatchRecord("CId", List("line1", "line2").mkString("\n"), List("IcId"), "OcId",
      LocalDateTime.of(2015, 1, 1, 0, 0, 0), LocalDateTime.of(2015, 1, 12, 0, 0, 0))
  }

}