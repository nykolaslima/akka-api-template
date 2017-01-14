package br.com.akkaapitemplate.components.user

import scala.util.{Failure, Success}

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import br.com.akkaapitemplate.components.user.ActorMessages.{Fail, LoadById}
import br.com.akkaapitemplate.infrastructure.logs.GelfLogger
import br.com.akkaapitemplate.infrastructure.logs.ApplicationError._

class UserRepositoryActor extends Actor with ActorLogging {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.warning(GelfLogger.warn(s"Restarting Actor due: ${reason.getMessage}", Map("internal_operation" -> ACTOR_RESTARTING), fullMessage = Some(reason.toString)))
  }

  def receive = LoggingReceive {
    case LoadById(requestId, originalSender, id) =>
      val replyTo = originalSender.getOrElse(sender())
      log.info(GelfLogger.buildWithRequestId(requestId).info(s"Requesting to load the User id: $id"))
      UserRepository.loadById(id).onComplete {
        case Success(s) =>
          log.info(GelfLogger.buildWithRequestId(requestId).info(s"Loading an optional User id: $id and replying to original sender"))
          replyTo ! s
        case Failure(f) =>
          log.error(GelfLogger.buildWithRequestId(requestId).error(s"Failed to load the User id: $id", Map(), fullMessage = Some(f.toString)), f)
          replyTo ! akka.actor.Status.Failure(f)
          self ! Fail(f)
      }
  }
}

object UserRepositoryActor {
  def props: Props = Props[UserRepositoryActor]
}
