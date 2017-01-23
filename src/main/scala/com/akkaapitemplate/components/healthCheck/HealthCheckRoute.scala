package com.akkaapitemplate.components.healthCheck

import scala.concurrent.duration._

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._

import akka.actor.ActorSystem
import akka.util.Timeout
import com.akkaapitemplate.infrastructure.routes.ApplicationRoute

trait HealthCheckRoute extends ApplicationRoute {
  implicit def actorSystem: ActorSystem
  implicit val timeout = Timeout(1.second)

  val routes = {
    path("health-check") {
      get {
        complete(OK)
      }
    }
  }

}
