package carldata.hs

import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeRecord, RemoveAction}
import carldata.hs.avro.{RealTimeRecordActionAvro, RealTimeRecordAvro}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import spray.json._

import scala.collection.JavaConverters.seqAsJavaList

object RealTimeCheck extends Properties("RealTime") {

  private val genScript = for {
    ls <- Gen.listOf(Gen.alphaNumStr)
  } yield ls.mkString("\n").trim

  private val realtimeRecordGen = for {
    action <- Gen.oneOf(AddAction, RemoveAction)
    calculation <- Gen.identifier
    script <- genScript
    trigger <- Gen.identifier
    outputChannel <- Gen.identifier
  } yield RealTimeRecord(action, calculation, script, trigger, outputChannel)

  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(realtimeRecordGen) { record: RealTimeRecord =>
    val source: String = record.toJson.compactPrint
    val r2 = source.parseJson.convertTo[RealTimeRecord]
    r2 == record
  }

  /** Check avro compatibility */
  property("AVRO") = forAll(realtimeRecordGen) { rec: RealTimeRecord =>
    val avro: RealTimeRecordAvro = new RealTimeRecordAvro(
      RealTimeRecordActionAvro.valueOf(rec.action.toString),
      rec.calculationId,
      seqAsJavaList(rec.script.split("\n")),
      rec.trigger,
      rec.outputChannelId)
    val avroStr = avro.toString
    avroStr.parseJson.convertTo[RealTimeRecord] == rec
  }
}
