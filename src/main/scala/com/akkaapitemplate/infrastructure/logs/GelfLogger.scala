package com.akkaapitemplate.infrastructure.logs

import com.akkaapitemplate.infrastructure.logs.SysLogLevel.SysLogLevel
import java.util.UUID
import org.joda.time.DateTime
import org.json4s.jackson.Serialization._

/**
  * Based on the GELF (Graylog Extended Log Format) specification, this class
  * brings to you logging with traceability.
  *
  * This was designed on top of the Akka Logging Framework.
  * Logging is performed asynchronously to ensure that logging has minimal performance impact.
  * Logging generally means IO and locks, which can slow down the operations of your code if it was performed synchronously.
  *
  * The log information is converted into json format:
  *
  * {
  *   "product":"property-quality",
  *   "application":"niemeyer-api",
  *   "environment":"dev",
  *   "version":"1.0",
  *   "timestamp":1476965570596,
  *   "level":3,
  *   "log_type":"application",
  *   "short_message":"Processing id 123 in state ACTIVE",
  *   "full_message":"Processing id 123 in state ACTIVE",
  *   "request_id":"9d8c7896-7e21-4921-ad50-3c8ca440a787"
  * }
  *
  * JSON format has emerged as the de facto standard for message passing.
  * It is both readable and reasonably compact, and it provides a standardized format for structuring data.
  *
  * Logging using the JSON format allows you to easily create and parse custom fields of your applications.
  *
  */
class GelfLogger(requestId: Option[UUID] = None) {

  def info(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = this.apply(message, fullMessage, SysLogLevel.INFO, mapArgs)
  def debug(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = this.apply(message, fullMessage, SysLogLevel.DEBUG, mapArgs)
  def warn(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = this.apply(message, fullMessage, SysLogLevel.WARNING, mapArgs)
  def error(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = this.apply(message, fullMessage, SysLogLevel.ERROR, mapArgs)

  private def apply(message: String, fullMessage: Option[String] = None, logLevel: SysLogLevel, mapArgs: Map[String, Any] = Map()) = {
    import com.akkaapitemplate.infrastructure.serialization.json.MyJsonProtocol._

    val default = Map(
      "timestamp" -> new DateTime().getMillis,
      "level" -> logLevel.id,
      "log_type" -> "application",
      "short_message" -> message,
      "full_message" -> fullMessage.getOrElse(message),
      "request_id" -> requestId.getOrElse("")
    )

    val customMap = mapArgs.map(tuple => s"_${tuple._1}" -> tuple._2)
    write(default ++ customMap)
  }
}


object GelfLogger {

  def buildWithRequestId(requestId: UUID) = new GelfLogger(Some(requestId))

  def info(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = new GelfLogger().info(message, mapArgs, fullMessage)
  def debug(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = new GelfLogger().debug(message, mapArgs, fullMessage)
  def warn(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = new GelfLogger().warn(message, mapArgs, fullMessage)
  def error(message: String, mapArgs: Map[String, Any] = Map(), fullMessage: Option[String] = None) = new GelfLogger().error(message, mapArgs, fullMessage)
}

/**
  * https://en.wikipedia.org/wiki/Syslog#Severity_level
  */
object SysLogLevel extends Enumeration {
  type SysLogLevel = Value
  val EMERGENCY = Value(0, "EMERGENCY")
  val ALERT = Value(1, "ALERT")
  val CRITICAL = Value(2, "CRITICAL")
  val ERROR = Value(3, "ERROR")
  val WARNING = Value(4, "WARNING")
  val NOTICE = Value(5, "NOTICE")
  val INFO = Value(6, "INFO")
  val DEBUG = Value(7, "DEBUG")
}
