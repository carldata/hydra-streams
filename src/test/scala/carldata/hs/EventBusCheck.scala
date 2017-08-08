package carldata.hs

import carldata.hs.EventBus.EventBusRecordJsonProtocol._
import carldata.hs.EventBus.{BatchCalculationStarted, BatchCalculationStopped, EventBusRecord, Status}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import spray.json._

/**
  * Property checks for DataRecord
  */
object EventBusCheck extends Properties("EventBus") {

  private val dataRecordGen = for {
    calculationId <- Gen.identifier
    status <- Gen.oneOf(BatchCalculationStarted, BatchCalculationStopped)
  } yield EventBusRecord(calculationId, status)


  /** Record serialized to json and then parsed back should be the same */
  property("parse") = forAll(dataRecordGen) { record: EventBusRecord =>
    val source: String = record.toJson.prettyPrint
    source.parseJson.convertTo[EventBusRecord] == record
  }

}
