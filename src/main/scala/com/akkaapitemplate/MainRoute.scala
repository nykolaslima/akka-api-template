package com.akkaapitemplate

import akka.actor.ActorSystem
import com.akkaapitemplate.components.user.UserRoute
import org.slf4j.LoggerFactory

class MainRoute()(implicit system: ActorSystem) {
  val log = LoggerFactory.getLogger(this.getClass)

  val user = new UserRoute {
    override implicit def actorSystem: ActorSystem = system
  }

  val routes = user.routes
}
