package com.akkaapitemplate.components.user

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import com.akkaapitemplate.components.user.ActorMessages.{Create, Fail, LoadById}
import com.akkaapitemplate.infrastructure.logs.ApplicationError._
import com.akkaapitemplate.infrastructure.logs.GelfLogger

class UserRepositoryActor(userRepository: UserRepository = UserRepository) extends Actor with ActorLogging {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.warning(GelfLogger.warn(s"Restarting Actor due: ${reason.getMessage}",
      Map("internal_operation" -> ACTOR_RESTARTING), fullMessage = Some(reason.toString)))
  }

  def receive = LoggingReceive {
    case LoadById(requestId, originalSender, id) =>
      val replyTo = originalSender.getOrElse(sender())

      userRepository.loadById(id).onComplete {
        case Success(message) =>
          log.info(GelfLogger.buildWithRequestId(requestId)
            .info(s"LoadById - Loaded an optional User id: $id and replying to sender"))

          replyTo ! message

        case Failure(message) =>
          log.error(GelfLogger.buildWithRequestId(requestId)
            .error(s"LoadById - Failed to load the User id: $id", Map(), fullMessage = Some(message.toString)), message)

          replyTo ! akka.actor.Status.Failure(message)
          self ! Fail(message)
      }

    case Create(requestId, originalSender, user) =>
      val replyTo = originalSender.getOrElse(sender())

      userRepository.add(user).onComplete {
        case Success(message) =>
          log.info(GelfLogger.buildWithRequestId(requestId)
            .info(s"Create - Added User: $user and replying to sender"))

          replyTo ! Right(message)

        case Failure(message) =>
          log.error(GelfLogger.buildWithRequestId(requestId)
            .error(s"Create - Failed to add the User: $user", Map(), fullMessage = Some(message.toString)), message)

          replyTo ! akka.actor.Status.Failure(message)
          self ! Fail(message)
      }

    case Fail(cause) =>
      log.warning(GelfLogger.warn(s"Let this crash and recovery: ${cause.getMessage}"))
      throw cause

    case x: Any => log.warning(GelfLogger.warn(s"Unknown message: $x", Map("internal_operation" -> UNKNOWN_MESSAGE)))
  }
}

object UserRepositoryActor {
  def props: Props = Props(new UserRepositoryActor())
}
