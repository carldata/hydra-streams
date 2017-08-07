package carldata.hs

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import carldata.hs.impl.JsonConverters._

/**
  * This record is used to send channel data points.
  */
object Data {

  case class DataRecord(channel: String, ts: LocalDateTime, value: Float)

  object DataJsonProtocol extends DefaultJsonProtocol {
    implicit object DataJsonFormat extends RootJsonFormat[DataRecord] {
      def write(r: DataRecord) =
        JsObject("channel" -> JsString(r.channel),
          "timestamp" -> JsString(r.ts.format(DateTimeFormatter.ISO_DATE_TIME)),
          "value" -> JsNumber(r.value))

      def read(value: JsValue): DataRecord = value match {
        case JsObject(fs) =>
          val channel: String = fs.get("channel").map(stringFromValue).getOrElse("")
          val ts: LocalDateTime = fs.get("timestamp").map(timestampFromValue).getOrElse(LocalDateTime.now())
          val v: Float = fs.get("value").map(floatFromValue).getOrElse(0f)
          DataRecord(channel, ts, v)
        case _ => DataRecord("json-format-error", LocalDateTime.now(), 0f)
      }
    }
  }
}
