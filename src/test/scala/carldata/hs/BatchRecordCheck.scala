package carldata.hs

import java.time.LocalDateTime

import carldata.hs.Batch.BatchRecord
import carldata.hs.Batch.BatchRecordJsonProtocol._
import carldata.hs.avro.BatchRecordAvro
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import spray.json._

import scala.collection.JavaConverters._

object BatchRecordCheck extends Properties("Batch")  {

  private val genScript = for {
    ls <- Gen.listOf(Gen.alphaNumStr)
  } yield ls.mkString("\n").trim

  private val batchRecordGen = for {
    calculationId <- Gen.identifier
    script <- genScript
    inputChannelId <- Gen.identifier
    outputChannelId <- Gen.identifier
  } yield BatchRecord(calculationId, script, inputChannelId, outputChannelId, LocalDateTime.now(), LocalDateTime.now())

  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(batchRecordGen) { record: BatchRecord =>
    val source: String = record.toJson.prettyPrint
    source.parseJson.convertTo[BatchRecord] == record
  }

  /** Check avro compatibility */
  property("AVRO") = forAll(batchRecordGen) { rec: BatchRecord =>
    val avro: BatchRecordAvro = new BatchRecordAvro(
      rec.calculationId,
      seqAsJavaList(rec.script.split("\n")),
      rec.inputChannelId,
      rec.outputChannelId,
      rec.startDate.toString,
      rec.endDate.toString)
    val avroStr = avro.toString
    avroStr.parseJson.convertTo[BatchRecord] == rec
  }
}
