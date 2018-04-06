package carldata.hs

import java.time.ZonedDateTime

import carldata.hs.DeleteData.DeleteDataJsonProtocol._
import carldata.hs.DeleteData.DeleteDataRecord
import carldata.hs.avro.DeleteDataAvro
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import spray.json._


object DeleteDataCheck extends Properties("DeleteData") {

  private val ddRecordGen = for {
    id <- Gen.identifier
    channelId <- Gen.identifier
  } yield DeleteDataRecord(id, channelId, ZonedDateTime.now, ZonedDateTime.now)

  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(ddRecordGen) { record: DeleteDataRecord =>
    val source: String = record.toJson.prettyPrint
    source.parseJson.convertTo[DeleteDataRecord] == record
  }

  /** Check avro compatibility */
  property("AVRO") = forAll(ddRecordGen) { rec: DeleteDataRecord =>
    val avro: DeleteDataAvro = new DeleteDataAvro(
      rec.actionId,
      rec.channelId,
      rec.startDate.toString,
      rec.endDate.toString)
    val avroStr = avro.toString
    avroStr.parseJson.convertTo[DeleteDataRecord] == rec
  }
}
