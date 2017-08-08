package carldata.hs

import java.time.LocalDateTime

import carldata.hs.Batch.BatchRecord
import carldata.hs.Batch.BatchRecordJsonProtocol._
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
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

  "BatchRecord" should "parse json version 1" in {
    val source =
      """
        |{"calculationId": "CId", "script": "S", "inputChannelId": "IcId", "outputChannelId" : "OcId", "startDate" : "2015-01-01T00:00:00", "endDate": "2015-01-01T00:00:00" }
      """.stripMargin
    val rec = JsonParser(source).convertTo[BatchRecord]
    rec shouldBe BatchRecord("CId", "S", "IcId", "OcId", LocalDateTime.of(2015, 1, 1, 0, 0, 0), LocalDateTime.of(2015, 1, 1, 0, 0, 0))
  }

}