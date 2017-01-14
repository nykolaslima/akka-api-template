package br.com.akkaapitemplate.infrastructure.serialization.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.ext.{JavaTypesSerializers, JodaTimeSerializers}
import org.json4s.{DefaultFormats, jackson}
import spray.json.DefaultJsonProtocol

object MyJsonProtocol extends Json4sSupport with DefaultJsonProtocol with SprayJsonSupport {
  implicit val formats = {
    DefaultFormats ++
      JodaTimeSerializers.all ++
      JavaTypesSerializers.all +
      new JsonStringProtocol
  }

  implicit val serialization = jackson.Serialization
}
