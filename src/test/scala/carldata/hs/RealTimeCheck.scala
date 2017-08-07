package carldata.hs

import spray.json._
import carldata.hs.RealTime.RealTimeRecord
import carldata.hs.RealTime.RealTimeJsonProtocol._
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

object RealTimeCheck extends Properties("RealTime") {
  private val realtimeRecordGen = for {
    action <- Gen.identifier
    calculation <- Gen.identifier
    script <- Gen.identifier
    trigger <- Gen.identifier
    outputChannel <- Gen.identifier
  } yield RealTimeRecord(action, calculation, script, trigger, outputChannel)

  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(realtimeRecordGen) { record: RealTimeRecord =>
    val source: String = record.toJson.prettyPrint
    source.parseJson.convertTo[RealTimeRecord] == record
  }
}
