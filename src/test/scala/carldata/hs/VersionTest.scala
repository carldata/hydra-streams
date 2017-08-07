package carldata.hs

import java.time.LocalDateTime

import carldata.hs.Data.DataRecord
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.RealTime.RealTimeRecord
import carldata.hs.RealTime.RealTimeJsonProtocol._
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
        |{"action": "A", "calculation": "C", "script": "S", "trigger" : "T", "outputChannel" : "oC"}
      """.stripMargin
    val rec = JsonParser(source).convertTo[RealTimeRecord]
    rec shouldBe RealTimeRecord("A","C","S","T","oC")
  }

}