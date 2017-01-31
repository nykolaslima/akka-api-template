package com.nykolaslima.akkaapitemplate

import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.nykolaslima.akkaapitemplate.infrastructure.config.AppConfig._
import org.slf4j.LoggerFactory

object Boot extends App {
  implicit val system = ActorSystem("akkaapitemplate")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val log = LoggerFactory.getLogger(this.getClass)

  val mainRoute = new MainRoute()

  Http().bindAndHandle(mainRoute.routes, config.getString("http.interface"), config.getInt("http.port")).onComplete {
    case Success(s) => log.info("Application Started")
    case Failure(f) => log.error("Could not start the server", f)
  }
}
