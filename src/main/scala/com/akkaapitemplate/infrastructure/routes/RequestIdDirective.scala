package com.akkaapitemplate.infrastructure.routes

import akka.http.scaladsl.server.Directives._
import java.util.UUID

trait RequestIdDirective {
  def extractRequestId = optionalHeaderValueByName("X-Request-Id").map {
    case Some(id) => UUID.fromString(id)
    case None => UUID.randomUUID()
  }
}
