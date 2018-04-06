package carldata.hs.impl

import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import spray.json.{JsArray, JsNumber, JsString, JsValue}

/**
  * Custom function for simplify json access
  */
object JsonConverters {

  def stringFromValue(jsVal: JsValue): String = jsVal match {
    case JsString(str) => str
    case v: JsValue => v.toString
  }

  def timestampFromValue(jsVal: JsValue): ZonedDateTime = jsVal match {
    case JsString(str) =>
      try {
        dateParse(str)
      } catch {
        case _: Exception =>
          ZonedDateTime.now
      }
    case _ => ZonedDateTime.now
  }

  def floatFromValue(jsVal: JsValue): Float = jsVal match {
    case JsNumber(v) => v.toFloat
    case _ => Float.NaN
  }

  def textFromLines(jsVal: JsValue): String = jsVal match {
    case JsArray(vs) => vs.map(stringFromValue).mkString("\n")
    case _ => ""
  }

  def dateParse(str: String): ZonedDateTime = {
    val formatter = new DateTimeFormatterBuilder()
      .parseCaseInsensitive
      .appendValue(ChronoField.YEAR)
      .appendLiteral('-')
      .appendValue(ChronoField.MONTH_OF_YEAR)
      .appendLiteral('-')
      .appendValue(ChronoField.DAY_OF_MONTH)
      .optionalStart.appendLiteral(' ').optionalEnd
      .optionalStart.appendLiteral('T').optionalEnd
      .optionalStart
      .appendValue(ChronoField.HOUR_OF_DAY)
      .appendLiteral(':')
      .appendValue(ChronoField.MINUTE_OF_HOUR)
      .optionalStart.appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE).optionalEnd
      .optionalStart.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd
      .optionalStart.appendLiteral('Z').optionalEnd
      .optionalEnd
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
      .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
      .toFormatter

    if (str.contains('[')) {
      ZonedDateTime.parse(str, DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }
    else ZonedDateTime.of(LocalDateTime.parse(str, formatter), ZoneId.of("UCT"))

  }

  def arrayFromValue(jsVal: JsValue): Seq[String] = jsVal match {
    case JsArray(vs) => vs.map(stringFromValue)
    case _ => Seq()
  }

}
