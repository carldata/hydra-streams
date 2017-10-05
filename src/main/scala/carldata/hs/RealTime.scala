package carldata.hs

import carldata.hs.impl.JsonConverters._
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, JsValue, RootJsonFormat}

object RealTime {

  sealed trait Action

  case object AddAction extends Action

  case object RemoveAction extends Action

  case object ErrorAction extends Action

  case class RealTimeJobRecord(action: Action, calculationId: String, script: String,
                               inputChannelIds: Seq[String], outputChannelId: String)

  object RealTimeJsonProtocol extends DefaultJsonProtocol {

    implicit object RealTimeJsonFormat extends RootJsonFormat[RealTimeJobRecord] {
      def write(r: RealTimeJobRecord) =
        JsObject(
          r.action match {
            case AddAction => "action" -> JsString("AddAction")
            case RemoveAction => "action" -> JsString("RemoveAction")
            case ErrorAction => "action" -> JsString("error")
          },
          "calculationId" -> JsString(r.calculationId),
          "script" -> JsArray(r.script.split("\n").map(JsString.apply).toVector),
          "inputChannelIds" -> JsArray(r.inputChannelIds.map(JsString.apply).toVector),
          "outputChannelId" -> JsString(r.outputChannelId)
        )

      def read(value: JsValue): RealTimeJobRecord = value match {
        case JsObject(fs) =>
          val action: Action = fs.get("action").map {
            case JsString("AddAction") => AddAction
            case JsString("RemoveAction") => RemoveAction
            case _ => ErrorAction
          }.getOrElse(ErrorAction)
          val calculation: String = fs.get("calculationId").map(stringFromValue).getOrElse("")
          val script: String = fs.get("script").map(textFromLines).getOrElse("")
          val inputChannelIds: Seq[String] = fs.get("inputChannelIds").map(arrayFromValue).getOrElse(Seq())
          val outputChannel: String = fs.get("outputChannelId").map(stringFromValue).getOrElse("")
          RealTimeJobRecord(action, calculation, script, inputChannelIds, outputChannel)
        case _ => RealTimeJobRecord(ErrorAction, "", value.toString, Seq(), "")
      }
    }

  }

}
