package com.akkaapitemplate.infrastructure.test

import akka.http.scaladsl.model.MediaType.Compressible
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{MediaType, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest

trait RouteSpec extends IntegrationSpec with ScalatestRouteTest {
  val acceptJson = Accept(MediaTypes.`application/json`)
  val acceptProto = Accept(MediaType.applicationBinary("x-protobuf", Compressible, "proto"))
}
