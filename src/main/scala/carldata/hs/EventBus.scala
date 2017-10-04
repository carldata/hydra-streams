package carldata.hs

import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat}

import carldata.hs.impl.JsonConverters._

object EventBus {
  sealed trait Status

  case object Started extends Status
  case object Stopped extends Status
  case object Other extends Status

  case class EventBusRecord(source: String, id: String, status: Status, message: String)

  object EventBusRecordJsonProtocol extends DefaultJsonProtocol {

    implicit object EventBusRecordJsonFormat extends RootJsonFormat[EventBusRecord] {
      def write(r: EventBusRecord) =
        JsObject(
          "source" -> JsString(r.source),
          "id" -> JsString(r.id),
          "status" -> JsString(r.status match {
            case Started => "Started"
            case Stopped => "Stopped"
            case Other => ""
          }),
          "msg" -> JsString(r.message)
        )

      def read(value: JsValue): EventBusRecord = value match {
        case JsObject(fs) =>
          val source: String = fs.get("source").map(stringFromValue).getOrElse("")
          val id: String = fs.get("id").map(stringFromValue).getOrElse("")
          val status: Status = fs.get("status").map {
            case JsString("Started") => Started
            case JsString("Stopped") => Stopped
            case _ => Other
          }.getOrElse(Other)
          val msg: String = fs.get("msg").map(stringFromValue).getOrElse("")
          EventBusRecord(source, id, status, msg)
        case _ => EventBusRecord("Json parser", "error", Other, value.toString())
      }
    }
  }
}