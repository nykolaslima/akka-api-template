package com.nykolaslima.akkaapitemplate.components.user

import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.nykolaslima.akkaapitemplate.components.user.ActorMessages.{Create, LoadById}
import com.nykolaslima.akkaapitemplate.infrastructure.logs.GelfLogger
import com.nykolaslima.akkaapitemplate.infrastructure.routes.ApplicationRoute
import com.nykolaslima.akkaapitemplate.infrastructure.validation.Rejection
import com.nykolaslima.akkaapitemplate.resources.{ErrorResource, RejectionResource, RejectionsResource, UserResource}
import org.slf4j.LoggerFactory

trait UserRoute extends ApplicationRoute {
  implicit def actorSystem: ActorSystem
  implicit val timeout = Timeout(1.second)

  val log = LoggerFactory.getLogger(this.getClass)
  val userServiceActor = actorSystem.actorOf(UserServiceActor.props, "user-service-actor")

  val routes = {
    path("users") {
      extractRequestId { requestId =>
        post {
          entity(as[UserResource]) { resource =>
            val user = User(None, resource.name)
            val actorResponse = (userServiceActor ? Create(requestId = requestId, user = user)).mapTo[Either[List[Rejection], User]]

            onComplete(actorResponse) {
              case Success(result) =>
                result match {
                  case Right(addedUser) =>
                    log.info(GelfLogger.buildWithRequestId(requestId).info("User added successfully"))
                    val resource = UserResource(addedUser.id.get.toString, addedUser.name)
                    complete((Created, resource))

                  case Left(rejections) =>
                    def toResource(rejection: Rejection): RejectionResource = RejectionResource(
                      rejection.category.toString,
                      rejection.target,
                      rejection.message,
                      rejection.key,
                      rejection.args.map(_.toString)
                    )
                    val resource = RejectionsResource(rejections.map(toResource))
                    complete((BadRequest, resource))
                }

              case Failure(e) =>
                log.error(GelfLogger.buildWithRequestId(requestId)
                  .error(message = s"Error adding user: ${e.getMessage}", fullMessage = Some(e.toString)))

                complete((InternalServerError, ErrorResource(requestId.toString, e.getMessage)))
            }
          }
        }
      }
    } ~
      path("users" / JavaUUID) { id =>
        extractRequestId { requestId =>
          get {
            val actorResponse = (userServiceActor ? LoadById(requestId = requestId, id = id)).mapTo[Option[User]]

            onComplete(actorResponse) {
              case Success(userOpt) => {
                userOpt match {
                  case Some(user) =>
                    log.info(GelfLogger.buildWithRequestId(requestId).info("User retrieved successfully"))
                    val resource = UserResource(user.id.get.toString, user.name)
                    complete((OK, resource))

                  case None =>
                    log.info(GelfLogger.buildWithRequestId(requestId).info("User not found"))
                    complete(NotFound)
                }
              }

              case Failure(e) => {
                log.error(GelfLogger.buildWithRequestId(requestId)
                  .error(message = s"Error fetching user: ${e.getMessage}", fullMessage = Some(e.toString)))

                complete((InternalServerError, ErrorResource(requestId.toString, e.getMessage)))
              }
            }
          }
        }
      }
  }
}
