package carldata.hs

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import carldata.hs.impl.JsonConverters._
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, JsValue, RootJsonFormat}

object RealTime {

  sealed trait RealTimeJob

  case class AddRealTimeJob(calculationId: String, script: String, inputChannelIds: Seq[String], outputChannelId: String,
                            startDate: ZonedDateTime, endDate: ZonedDateTime) extends RealTimeJob

  case class RemoveRealTimeJob(calculationId: String) extends RealTimeJob

  object RealTimeJsonProtocol extends DefaultJsonProtocol {

    implicit object realTimeJsonFormat extends RootJsonFormat[RealTimeJob] {
      def write(job: RealTimeJob): JsObject = {
        job match {
          case r: AddRealTimeJob =>
            JsObject(
              "action" -> JsString("AddAction"),
              "calculationId" -> JsString(r.calculationId),
              "script" -> JsArray(r.script.split("\n").map(JsString.apply).toVector),
              "inputChannelIds" -> JsArray(r.inputChannelIds.map(JsString.apply).toVector),
              "outputChannelId" -> JsString(r.outputChannelId),
              "startDate" -> JsString(r.startDate.format(DateTimeFormatter.ISO_DATE_TIME)),
              "endDate" -> JsString(r.endDate.format(DateTimeFormatter.ISO_DATE_TIME))
            )
          case r: RemoveRealTimeJob =>
            JsObject(
              "action" -> JsString("RemoveAction"),
              "calculationId" -> JsString(r.calculationId)
            )
        }
      }

      def read(value: JsValue): RealTimeJob = value match {
        case JsObject(fs) =>
          fs.get("action").map {
            case JsString("RemoveAction") =>
              val calculation: String = fs.get("calculationId").map(stringFromValue).getOrElse("")
              RemoveRealTimeJob(calculation)

            case _ =>
              val calculation: String = fs.get("calculationId").map(stringFromValue).getOrElse("")
              val script: String = fs.get("script").map(textFromLines).getOrElse("")
              val inputChannelIds: Seq[String] = fs.get("inputChannelIds").map(arrayFromValue).getOrElse(Seq())
              val outputChannel: String = fs.get("outputChannelId").map(stringFromValue).getOrElse("")
              val startDate: ZonedDateTime = fs.get("startDate").map(timestampFromValue).getOrElse(ZonedDateTime.now)
              val endDate: ZonedDateTime = fs.get("endDate").map(timestampFromValue).getOrElse(ZonedDateTime.now)
              AddRealTimeJob(calculation, script, inputChannelIds, outputChannel, startDate, endDate)
          }.getOrElse(AddRealTimeJob("", "", Seq(), "", ZonedDateTime.now, ZonedDateTime.now))
      }
    }

  }

}
