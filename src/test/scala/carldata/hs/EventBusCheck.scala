package carldata.hs

import carldata.hs.EventBus.EventBusRecordJsonProtocol._
import carldata.hs.EventBus.{EventBusRecord, Started, Stopped}
import carldata.hs.avro.EventBusRecordAvro
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import spray.json._

/**
  * Property checks for DataRecord
  */
object EventBusCheck extends Properties("EventBus") {

  private val dataRecordGen = for {
    source <- Gen.identifier
    id <- Gen.identifier
    status <- Gen.oneOf(Started, Stopped)
    msg <- Gen.alphaStr
  } yield EventBusRecord(source, id, status, msg)


  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(dataRecordGen) { record: EventBusRecord =>
    val source: String = record.toJson.prettyPrint
    source.parseJson.convertTo[EventBusRecord] == record
  }

  /** Check avro compatibility */
  property("AVRO") = forAll(dataRecordGen) { rec: EventBusRecord =>
    val avro: EventBusRecordAvro = new EventBusRecordAvro(
      rec.source, rec.id, rec.status.toString, rec.message)
    val avroStr = avro.toString
    avroStr.parseJson.convertTo[EventBusRecord] == rec
  }
}
