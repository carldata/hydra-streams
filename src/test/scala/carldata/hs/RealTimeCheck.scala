package carldata.hs

import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeJobRecord, RemoveAction}
import carldata.hs.avro.{RealTimeJobAvro, RealTimeRecordActionAvro}
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
    inputChannelId <- Gen.listOf(Gen.identifier)
    outputChannel <- Gen.identifier
  } yield RealTimeJobRecord(action, calculation, script, inputChannelId, outputChannel)

  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(realtimeRecordGen) { record: RealTimeJobRecord =>
    val source: String = record.toJson.compactPrint
    val r2 = source.parseJson.convertTo[RealTimeJobRecord]
    r2 == record
  }

  /** Check avro compatibility */
  property("AVRO") = forAll(realtimeRecordGen) { rec: RealTimeJobRecord =>
    val avro = new RealTimeJobAvro(
      rec.action.toString,
      rec.calculationId,
      seqAsJavaList(rec.script.split("\n")),
      seqAsJavaList(rec.inputChannelIds),
      rec.outputChannelId)
    val avroStr = avro.toString
    avroStr.parseJson.convertTo[RealTimeJobRecord] == rec
  }
}
