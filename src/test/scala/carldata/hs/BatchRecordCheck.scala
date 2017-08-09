package carldata.hs

import java.time.LocalDateTime

import spray.json._
import carldata.hs.Batch.BatchRecord
import carldata.hs.Batch.BatchRecordJsonProtocol._
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

object BatchRecordCheck extends Properties("Batch")  {

  private val genScript = for {
    ls <- Gen.listOf(Gen.alphaNumStr)
  } yield ls.mkString("\n")

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
}
