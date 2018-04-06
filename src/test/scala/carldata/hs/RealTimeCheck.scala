package carldata.hs

import java.time.ZonedDateTime

import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddRealTimeJob, RealTimeJob, RemoveRealTimeJob}
import carldata.hs.avro.RealTimeJobAvro
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import spray.json._

import scala.collection.JavaConverters.seqAsJavaList

object RealTimeCheck extends Properties("RealTime") {

  private val genScript = for {
    ls <- Gen.listOf(Gen.alphaNumStr)
  } yield ls.mkString("\n").trim

  private val addRealTimeJobGen = for {
    calculation <- Gen.identifier
    script <- genScript
    inputChannelId <- Gen.listOf(Gen.identifier)
    outputChannel <- Gen.identifier
  } yield AddRealTimeJob(calculation, script, inputChannelId, outputChannel, ZonedDateTime.now, ZonedDateTime.now)

  private val removeRealTimeJobGen = for {
    calculation <- Gen.identifier
  } yield RemoveRealTimeJob(calculation)

  private val realTimeJobGen = Gen.oneOf(addRealTimeJobGen, removeRealTimeJobGen)

  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(realTimeJobGen) { record: RealTimeJob =>
    val source: String = record.toJson.compactPrint
    val r2 = source.parseJson.convertTo[RealTimeJob]
    r2 == record
  }

  /** Check avro compatibility */
  property("AVRO") = forAll(realTimeJobGen) { job: RealTimeJob =>
    val avro = job match {
      case rec: AddRealTimeJob =>
        new RealTimeJobAvro("AddAction", rec.calculationId, seqAsJavaList(rec.script.split("\n")),
          seqAsJavaList(rec.inputChannelIds), rec.outputChannelId, rec.startDate.toString, rec.endDate.toString)
      case rec: RemoveRealTimeJob =>
        new RealTimeJobAvro("RemoveAction", rec.calculationId, seqAsJavaList(List()), seqAsJavaList(List()), "", "", "")
    }

    val avroStr = avro.toString
    avroStr.parseJson.convertTo[RealTimeJob] == job
  }
}
