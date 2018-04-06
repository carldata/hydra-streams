package carldata.hs

import java.time.ZonedDateTime

import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.hs.avro.DataAvro
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import spray.json._

/**
  * Property checks for DataAvro
  */
object DataCheck extends Properties("Data") {

  private val dataRecordGen = for {
    channel <- Gen.identifier
    value <- Gen.chooseNum(-1000f, 1000f)
  } yield DataRecord(channel, ZonedDateTime.now, value)


  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(dataRecordGen) { record: DataRecord =>
    val source: String = record.toJson.prettyPrint
    source.parseJson.convertTo[DataRecord] == record
  }

  /** Check avro compatibility */
  property("AVRO") = forAll(dataRecordGen) { rec: DataRecord =>
    val avro: DataAvro = new DataAvro(rec.channelId, rec.timestamp.toString, rec.value)
    val str: String = avro.toString
    str.parseJson.convertTo[DataRecord] == rec
  }

}
