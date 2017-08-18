package carldata.hs

import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}
import carldata.hs.impl.JsonConverters._

object RealTime {

  sealed trait Action

  case object AddAction extends Action

  case object RemoveAction extends Action

  case object UnknownAction extends Action

  case class RealTimeRecord(action: Action, calculationId: String, script: String, trigger: String, outputChannelId: String)

  object RealTimeJsonProtocol extends DefaultJsonProtocol {

    implicit object RealTimeJsonFormat extends RootJsonFormat[RealTimeRecord] {
      def write(r: RealTimeRecord) =
        JsObject(
          r.action match {
            case AddAction => "action" -> JsString("AddAction")
            case RemoveAction => "action" -> JsString("RemoveAction")
            case UnknownAction => "action" -> JsString("")
          },
          "calculationId" -> JsString(r.calculationId),
          "script" -> JsArray(r.script.split("\n").map(JsString.apply).toVector),
          "trigger" -> JsString(r.trigger),
          "outputChannelId" -> JsString(r.outputChannelId)
        )

      def read(value: JsValue): RealTimeRecord = value match {
        case JsObject(fs) =>
          val action: Action = fs.get("action").map {
            case JsString("AddAction") => AddAction
            case JsString("RemoveAction") => RemoveAction
            case _ => UnknownAction
          }.getOrElse(UnknownAction)
          val calculation: String = fs.get("calculationId").map(stringFromValue).getOrElse("")
          val script: String = fs.get("script").map(textFromLines).getOrElse("")
          val trigger: String = fs.get("trigger").map(stringFromValue).getOrElse("")
          val outputChannel: String = fs.get("outputChannelId").map(stringFromValue).getOrElse("")
          RealTimeRecord(action, calculation, script, trigger, outputChannel)
        case _ => RealTimeRecord(UnknownAction, "json-format-error", "", "", "")
      }
    }

  }

}
