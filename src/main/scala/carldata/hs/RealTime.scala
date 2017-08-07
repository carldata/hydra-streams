package carldata.hs

import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import carldata.hs.impl.JsonConverters._

object RealTime {

  case class RealTimeRecord(action: String, calculation: String, script: String, trigger: String, outputChannel: String)

  object RealTimeJsonProtocol extends DefaultJsonProtocol {

    implicit object RealTimeJsonFormat extends RootJsonFormat[RealTimeRecord] {
      def write(r: RealTimeRecord) =
        JsObject("action" -> JsString(r.action),
          "calculation" -> JsString(r.calculation),
          "script" -> JsString(r.script),
          "trigger" -> JsString(r.trigger),
          "outputChannel" -> JsString(r.outputChannel)
        )

      def read(value: JsValue): RealTimeRecord = value match {
        case JsObject(fs) =>
          val action: String = fs.get("action").map(stringFromValue).getOrElse("")
          val calculation: String = fs.get("calculation").map(stringFromValue).getOrElse("")
          val script: String = fs.get("script").map(stringFromValue).getOrElse("")
          val trigger: String = fs.get("trigger").map(stringFromValue).getOrElse("")
          val outputChannel: String = fs.get("outputChannel").map(stringFromValue).getOrElse("")
          RealTimeRecord(action, calculation, script, trigger, outputChannel)
        case _ => RealTimeRecord("json-format-error", "", "", "", "")
      }
    }

  }

}
