package carldata.hs.impl

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import spray.json.{JsArray, JsNumber, JsString, JsValue}

/**
  * Custom function for simplify json access
  */
object JsonConverters {

  def stringFromValue(jsVal: JsValue): String = jsVal match {
    case JsString(str) => str
    case v: JsValue => v.toString
  }

  def timestampFromValue(jsVal: JsValue): LocalDateTime = jsVal match {
    case JsString(str) =>
      try{
        LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME)
      } catch {
        case _: Exception =>
          LocalDateTime.now()
      }
    case _ => LocalDateTime.now()
  }

  def floatFromValue(jsVal: JsValue): Float = jsVal match {
    case JsNumber(v) => v.toFloat
    case _ => 0f
  }

  def textFromLines(jsVal: JsValue): String = jsVal match {
    case JsArray(vs) => vs.map(stringFromValue).mkString("\n")
    case _ => ""
  }

}
