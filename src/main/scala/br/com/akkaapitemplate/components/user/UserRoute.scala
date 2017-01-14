package br.com.akkaapitemplate.components.user

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import br.com.akkaapitemplate.components.user.ActorMessages.LoadById
import br.com.akkaapitemplate.infrastructure.logs.GelfLogger
import br.com.akkaapitemplate.infrastructure.routes.RequestIdDirective
import java.util.UUID
import org.slf4j.LoggerFactory

trait UserRoute extends RequestIdDirective {
  implicit def actorSystem: ActorSystem
  implicit val timeout = Timeout(1.second)
  val log = LoggerFactory.getLogger(this.getClass)
  val userServiceActor = actorSystem.actorOf(UserServiceActor.props, "user-service-actor")

  val routes = {
    pathPrefix("/users" / JavaUUID) { id =>
      get {
        extractRequestId { requestId =>
          val response = (userServiceActor ? LoadById(requestId = requestId, id = id)).mapTo[Option[User]]
          completeResponse(requestId, response)
        }
      }
    }
  }

  private def completeResponse(requestId: UUID, userFuture: Future[Option[User]]): Route = {
    onComplete(userFuture) {
      case Success(userOpt) =>
        userOpt match {
          case Some(user) =>
            log.info(GelfLogger.buildWithRequestId(requestId).info("User retrieved successfully"))
            complete((OK, user))

          case None =>
            log.info(GelfLogger.buildWithRequestId(requestId).info("User not found"))
            complete(NotFound)
        }

      case Failure(e) =>
        log.error(GelfLogger.buildWithRequestId(requestId).error(s"Error fetching condominium: ${e.getMessage}", Map(), fullMessage = Some(e.toString)))
        complete((InternalServerError, e.getMessage))
    }
  }

}
