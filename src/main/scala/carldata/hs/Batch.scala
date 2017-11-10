package carldata.hs

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import carldata.hs.impl.JsonConverters.{stringFromValue, textFromLines, timestampFromValue, arrayFromValue}
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, JsValue, RootJsonFormat}

object Batch {
  case class BatchRecord(calculationId: String, script: String, inputChannelIds: Seq[String], outputChannelId: String,
                         startDate: LocalDateTime, endDate: LocalDateTime)

  object BatchRecordJsonProtocol extends DefaultJsonProtocol {

    implicit object BatchRecordJsonFormat extends RootJsonFormat[BatchRecord] {
      def write(r: BatchRecord) =
        JsObject("calculationId" -> JsString(r.calculationId),
          "script" -> JsArray(r.script.split("\n").map(JsString.apply).toVector),
          "inputChannelIds" -> JsArray(r.inputChannelIds.map(JsString.apply).toVector),
          "outputChannelId" -> JsString(r.outputChannelId),
          "startDate" -> JsString(r.startDate.format(DateTimeFormatter.ISO_DATE_TIME)),
          "endDate" -> JsString(r.endDate.format(DateTimeFormatter.ISO_DATE_TIME))
        )

      def read(value: JsValue): BatchRecord = value match {
        case JsObject(fs) =>
          val calculationId: String = fs.get("calculationId").map(stringFromValue).getOrElse("")
          val script: String = fs.get("script").map(textFromLines).getOrElse("")
          val inputChannelId: Seq[String] = fs.get("inputChannelIds").map(arrayFromValue).getOrElse(Seq())
          val outputChannelId: String = fs.get("outputChannelId").map(stringFromValue).getOrElse("")
          val startDate: LocalDateTime = fs.get("startDate").map(timestampFromValue).getOrElse(LocalDateTime.now())
          val endDate: LocalDateTime = fs.get("endDate").map(timestampFromValue).getOrElse(LocalDateTime.now())
          BatchRecord(calculationId, script, inputChannelId, outputChannelId, startDate, endDate)
        case _ => BatchRecord("json-format-error", "json-format-error", Seq("json-format-error"), "json-format-error",
          LocalDateTime.now(), LocalDateTime.now())
      }
    }
  }
}
