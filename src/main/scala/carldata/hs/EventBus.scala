package carldata.hs

import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat}

import carldata.hs.impl.JsonConverters._

object EventBus {
  sealed trait Status

  case object BatchCalculationStarted extends Status
  case object BatchCalculationStopped extends Status
  case object BatchCalculationUnknown extends Status

  case class EventBusRecord(calculationId: String, status: Status)

  object EventBusRecordJsonProtocol extends DefaultJsonProtocol {

    implicit object BatchRecordJsonFormat extends RootJsonFormat[EventBusRecord] {
      def write(r: EventBusRecord) =
        JsObject("calculationId" -> JsString(r.calculationId),
          r.status match {
            case BatchCalculationStarted => "status" -> JsString("BatchCalculationStarted")
            case BatchCalculationStopped => "status" -> JsString("BatchCalculationStopped")
            case BatchCalculationUnknown => "status" -> JsString("")
          }
        )

      def read(value: JsValue): EventBusRecord = value match {
        case JsObject(fs) =>
          val calculationId: String = fs.get("calculationId").map(stringFromValue).getOrElse("")
          val status: Status = fs.get("status").map {
            case JsString("BatchCalculationStarted") => BatchCalculationStarted
            case JsString("BatchCalculationStopped") => BatchCalculationStopped
            case _ => BatchCalculationUnknown
          }.getOrElse(BatchCalculationUnknown)
          EventBusRecord(calculationId, status)
        case _ => EventBusRecord("json-format-error", BatchCalculationUnknown)
      }
    }
  }
}