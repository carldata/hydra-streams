package carldata.hs

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import carldata.hs.impl.JsonConverters.{stringFromValue, timestampFromValue}
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat}


object DeleteData {

  case class DeleteDataRecord(actionId: String, channelId: String, startDate: ZonedDateTime, endDate: ZonedDateTime)

  object DeleteDataJsonProtocol extends DefaultJsonProtocol {

    implicit object DeleteDataJsonFormat extends RootJsonFormat[DeleteDataRecord] {
      def write(r: DeleteDataRecord) =
        JsObject("actionId" -> JsString(r.actionId),
          "channelId" -> JsString(r.channelId),
          "startDate" -> JsString(r.startDate.format(DateTimeFormatter.ISO_DATE_TIME)),
          "endDate" -> JsString(r.endDate.format(DateTimeFormatter.ISO_DATE_TIME))
        )

      def read(value: JsValue): DeleteDataRecord = value match {
        case JsObject(fs) =>
          val actionId: String = fs.get("actionId").map(stringFromValue).getOrElse("")
          val channelId: String = fs.get("channelId").map(stringFromValue).getOrElse("")
          val startDate: ZonedDateTime = fs.get("startDate").map(timestampFromValue).getOrElse(ZonedDateTime.now)
          val endDate: ZonedDateTime = fs.get("endDate").map(timestampFromValue).getOrElse(ZonedDateTime.now)
          DeleteDataRecord(actionId, channelId, startDate, endDate)
        case _ => DeleteDataRecord("", "", ZonedDateTime.now, ZonedDateTime.now)
      }
    }

  }

}
