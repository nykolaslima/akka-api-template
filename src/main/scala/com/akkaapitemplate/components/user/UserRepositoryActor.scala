package com.akkaapitemplate.components.user

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import com.akkaapitemplate.components.user.ActorMessages.{Fail, LoadById}
import com.akkaapitemplate.infrastructure.logs.GelfLogger
import com.akkaapitemplate.infrastructure.logs.ApplicationError._

class UserRepositoryActor extends Actor with ActorLogging {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.warning(GelfLogger.warn(s"Restarting Actor due: ${reason.getMessage}",
      Map("internal_operation" -> ACTOR_RESTARTING), fullMessage = Some(reason.toString)))
  }

  def receive = LoggingReceive {
    case LoadById(requestId, originalSender, id) =>
      val replyTo = originalSender.getOrElse(sender())

      log.info(GelfLogger.buildWithRequestId(requestId).info(s"Requesting to load the User id: $id"))

      UserRepository.loadById(id).onComplete {
        case Success(message) =>
          log.info(GelfLogger.buildWithRequestId(requestId)
            .info(s"Loading an optional User id: $id and replying to original sender"))

          replyTo ! message

        case Failure(message) =>
          log.error(GelfLogger.buildWithRequestId(requestId)
            .error(s"Failed to load the User id: $id", Map(), fullMessage = Some(message.toString)), message)

          replyTo ! akka.actor.Status.Failure(message)
          self ! Fail(message)
      }
  }
}

object UserRepositoryActor {
  def props: Props = Props[UserRepositoryActor]
}
