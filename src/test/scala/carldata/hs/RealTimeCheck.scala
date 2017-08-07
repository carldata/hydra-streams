package carldata.hs

import spray.json._
import carldata.hs.RealTime.RealTimeRecord
import carldata.hs.RealTime.RealTimeJsonProtocol._
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}

object RealTimeCheck extends Properties("RealTime") {
  
  val genScript = for {
    spec <- Gen.oneOf(Seq('\n', '\t', ' '))
    unicode <- Arbitrary.arbString.arbitrary
    str <- Gen.alphaNumStr
  } yield List(spec, unicode, str).mkString

  private val realtimeRecordGen = for {
    action <- Gen.identifier
    calculation <- Gen.identifier
    script <- genScript
    trigger <- Gen.identifier
    outputChannel <- Gen.identifier
  } yield RealTimeRecord(action, calculation, script, trigger, outputChannel)

  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(realtimeRecordGen) { record: RealTimeRecord =>
    val source: String = record.toJson.prettyPrint
    source.parseJson.convertTo[RealTimeRecord] == record
  }
}
