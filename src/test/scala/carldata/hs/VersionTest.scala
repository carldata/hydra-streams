package carldata.hs

import java.time.LocalDateTime

import carldata.hs.Batch.BatchRecordJsonProtocol._
import carldata.hs.Batch.BatchRecord
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.hs.DeleteData.DeleteDataRecord
import carldata.hs.DeleteData.DeleteDataJsonProtocol._
import carldata.hs.EventBus.EventBusRecordJsonProtocol._
import carldata.hs.EventBus.{EventBusRecord, Started}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime._
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
        |{"channelId": "A", "timestamp": "2015-01-01T00:00:00Z", "value": null}
      """.stripMargin
    val rec = JsonParser(source).convertTo[DataRecord]
    rec.value.isNaN shouldBe true
  }

  "AddRealTimeJob" should "parse json version 1" in {
    val source =
      """
        |{
        |"action": "AddAction",
        |"calculationId": "C",
        |"script": ["line1", "line2"],
        |"inputChannelIds": ["IcId", "in2"],
        |"outputChannelId" : "oC",
        |"startDate" : "2015-01-01T00:00:00",
        |"endDate": "2015-01-01T00:00:00" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[RealTimeJob]
    rec shouldBe AddRealTimeJob("C", List("line1", "line2").mkString("\n"), List("IcId", "in2"), "oC",
      LocalDateTime.of(2015, 1, 1, 0, 0, 0), LocalDateTime.of(2015, 1, 1, 0, 0, 0))
  }

  "RemoveRealTimeJob" should "parse json version 1" in {
    val source =
      """
        |{
        |"action": "RemoveAction",
        |"calculationId": "C" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[RealTimeJob]
    rec shouldBe RemoveRealTimeJob("C")
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

  it should "parse json with date only" in {
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

  "DeleteRecord" should "parse json version 1" in {
    val source =
      """
        |{"actionId": "123",
        |"channelId" : "456",
        |"startDate" : "2015-01-01T00:00:00",
        |"endDate": "2015-01-01T00:00:00" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[DeleteDataRecord]
    rec shouldBe DeleteDataRecord("123", "456", LocalDateTime.of(2015, 1, 1, 0, 0, 0), LocalDateTime.of(2015, 1, 1, 0, 0, 0))
  }

}