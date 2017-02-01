package $organization$.$name__word$.infrastructure.test

import akka.http.scaladsl.model.MediaType.Compressible
import akka.http.scaladsl.model.headers.{Accept, `Content-Type`}
import akka.http.scaladsl.model.{MediaType, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest

trait RouteSpec extends IntegrationSpec with ScalatestRouteTest {
  val acceptJson = Accept(MediaTypes.`application/json`)
  val acceptProto = Accept(MediaType.applicationBinary("x-protobuf", Compressible, "proto"))
  val contentJson = `Content-Type`(MediaTypes.`application/json`)
  val contentProto = `Content-Type`(MediaType.applicationBinary("x-protobuf", Compressible, "proto"))
}
