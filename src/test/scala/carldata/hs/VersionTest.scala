package carldata.hs

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import carldata.hs.Batch.BatchRecord
import carldata.hs.Batch.BatchRecordJsonProtocol._
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.hs.DeleteData.DeleteDataJsonProtocol._
import carldata.hs.DeleteData.DeleteDataRecord
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

  "DataRecord" should "parse json version 1 without ZoneId" in {
    val source =
      """
        |{"channelId": "A", "timestamp": "2015-01-01T00:00:00", "value": 100}
      """.stripMargin
    val rec = JsonParser(source).convertTo[DataRecord]
    val ldt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe DataRecord("A", ZonedDateTime.of(ldt, ZoneId.of("UCT")), 100)
  }

  it should "parse json version 1 with ZoneId" in {
    val source =
      """
        |{"channelId": "A", "timestamp": "2015-01-01T00:00:00.000+02:00[Europe/Warsaw]", "value": 100}
      """.stripMargin
    val rec = JsonParser(source).convertTo[DataRecord]
    val ldt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe DataRecord("A", ZonedDateTime.of(ldt, ZoneId.of("Europe/Warsaw")), 100)
  }

  it should "parse null value" in {
    val source =
      """
        |{"channelId": "A", "timestamp": "2015-01-01T00:00:00Z", "value": null}
      """.stripMargin
    val rec = JsonParser(source).convertTo[DataRecord]
    rec.value.isNaN shouldBe true
  }

  "AddRealTimeJob" should "parse json version 1 without ZoneId" in {
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
    val startLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    val endLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe AddRealTimeJob("C", List("line1", "line2").mkString("\n"), List("IcId", "in2"), "oC",
      ZonedDateTime.of(startLdt, ZoneId.of("UCT")), ZonedDateTime.of(endLdt, ZoneId.of("UCT")))
  }

  it should "parse json version 1 with ZoneId" in {
    val source =
      """
        |{
        |"action": "AddAction",
        |"calculationId": "C",
        |"script": ["line1", "line2"],
        |"inputChannelIds": ["IcId", "in2"],
        |"outputChannelId" : "oC",
        |"startDate" : "2015-01-01T00:00:00.000+02:00[Europe/Warsaw]",
        |"endDate": "2015-01-01T00:00:00.000+02:00[Europe/Warsaw]" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[RealTimeJob]
    val startLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    val endLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe AddRealTimeJob("C", List("line1", "line2").mkString("\n"), List("IcId", "in2"), "oC",
      ZonedDateTime.of(startLdt, ZoneId.of("Europe/Warsaw")), ZonedDateTime.of(endLdt, ZoneId.of("Europe/Warsaw")))
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

  "BatchRecord" should "parse json version 1 without ZoneId" in {
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
    val startLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    val endLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe BatchRecord("CId", List("line1", "line2").mkString("\n"), List("IcId", "in2"), "OcId",
      ZonedDateTime.of(startLdt, ZoneId.of("UCT")), ZonedDateTime.of(endLdt, ZoneId.of("UCT")))
  }

  it should "parse json version 1 with ZoneId" in {
    val source =
      """
        |{"calculationId": "CId",
        |"script": ["line1", "line2"],
        |"inputChannelIds": ["IcId", "in2"],
        |"outputChannelId" : "OcId",
        |"startDate" : "2015-01-01T00:00:00.000+02:00[Europe/Warsaw]",
        |"endDate": "2015-01-01T00:00:00.000+02:00[Europe/Warsaw]" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[BatchRecord]
    val startLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    val endLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe BatchRecord("CId", List("line1", "line2").mkString("\n"), List("IcId", "in2"), "OcId",
      ZonedDateTime.of(startLdt, ZoneId.of("Europe/Warsaw")), ZonedDateTime.of(endLdt, ZoneId.of("Europe/Warsaw")))
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
    val startLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    val endLdt = LocalDateTime.of(2015, 1, 12, 0, 0, 0)
    rec shouldBe BatchRecord("CId", List("line1", "line2").mkString("\n"), List("IcId"), "OcId",
      ZonedDateTime.of(startLdt, ZoneId.of("UCT")), ZonedDateTime.of(endLdt, ZoneId.of("UCT")))
  }

  "DeleteRecord" should "parse json version 1 without ZoneId" in {
    val source =
      """
        |{"actionId": "123",
        |"channelId" : "456",
        |"startDate" : "2015-01-01T00:00:00",
        |"endDate": "2015-01-01T00:00:00" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[DeleteDataRecord]
    val startLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    val endLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe DeleteDataRecord("123", "456"
      , ZonedDateTime.of(startLdt, ZoneId.of("UCT")), ZonedDateTime.of(endLdt, ZoneId.of("UCT")))
  }

  it should "parse json version 1 with ZoneId" in {
    val source =
      """
        |{"actionId": "123",
        |"channelId" : "456",
        |"startDate" : "2015-01-01T00:00:00.000+02:00[Europe/Belgrade]",
        |"endDate": "2015-01-01T00:00:00.000+02:00[Europe/Belgrade]" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[DeleteDataRecord]
    val startLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    val endLdt = LocalDateTime.of(2015, 1, 1, 0, 0, 0)
    rec shouldBe DeleteDataRecord("123", "456"
      , ZonedDateTime.of(startLdt, ZoneId.of("Europe/Belgrade")), ZonedDateTime.of(endLdt, ZoneId.of("Europe/Belgrade")))
  }

}