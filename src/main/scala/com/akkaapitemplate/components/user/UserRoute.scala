package com.akkaapitemplate.components.user

import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.akkaapitemplate.components.user.ActorMessages.LoadById
import com.akkaapitemplate.infrastructure.logs.GelfLogger
import com.akkaapitemplate.infrastructure.routes.RequestIdDirective
import org.slf4j.LoggerFactory

trait UserRoute extends RequestIdDirective {
  implicit def actorSystem: ActorSystem
  implicit val timeout = Timeout(1.second)

  val log = LoggerFactory.getLogger(this.getClass)

  val userServiceActor = actorSystem.actorOf(UserServiceActor.props, "user-service-actor")

  val routes = {
    pathPrefix("users" / JavaUUID) { id =>
      get {
        extractRequestId { requestId =>
          val actorResponse = (userServiceActor ? LoadById(requestId = requestId, id = id)).mapTo[Option[User]]

          onComplete(actorResponse) {
            case Success(userOpt) => {
              userOpt match {
                case Some(user) =>
                  import com.akkaapitemplate.infrastructure.serialization.json.MyJsonProtocol._

                  log.info(GelfLogger.buildWithRequestId(requestId).info("User retrieved successfully"))
                  complete((StatusCodes.OK, user))

                case None =>
                  log.info(GelfLogger.buildWithRequestId(requestId).info("User not found"))
                  complete(StatusCodes.NotFound)
              }
            }

            case Failure(e) => {
              log.error(GelfLogger.buildWithRequestId(requestId)
                .error(message = s"Error fetching user: ${e.getMessage}", fullMessage = Some(e.toString)))

              complete((StatusCodes.InternalServerError, e.getMessage))
            }
          }
        }
      }
    }

  }
}
