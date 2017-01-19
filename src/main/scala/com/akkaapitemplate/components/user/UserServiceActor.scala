package com.akkaapitemplate.components.user

import scala.util.Left

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.event.LoggingReceive
import com.akkaapitemplate.components.user.ActorMessages.{Create, LoadById}
import com.akkaapitemplate.infrastructure.logs.ApplicationError._
import com.akkaapitemplate.infrastructure.logs.GelfLogger

class UserServiceActor(repositoryRef: Option[ActorRef] = None, userValidator: UserValidator = UserValidator) extends Actor with ActorLogging {
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: java.sql.SQLTimeoutException => Restart
    case _: org.postgresql.util.PSQLException => Stop
    case _: Exception => Restart
  }

  var userRepositoryActor: ActorRef = createRepository

  def receive = LoggingReceive {
    case loadById @ LoadById(requestId, _, id) =>
      log.info(GelfLogger.buildWithRequestId(requestId)
        .info(s"LoadById - Forwarding user id: $id to load"))

      userRepositoryActor forward loadById

    case create @ Create(requestId, originalSender, user) =>
      val replyTo = originalSender.getOrElse(sender())

      log.info(GelfLogger.buildWithRequestId(requestId)
        .info(s"Create - Validating user: $user"))
      userValidator.validate(user) match {
        case Nil =>
          log.info(GelfLogger.buildWithRequestId(requestId)
            .info(s"Create - Forwarding user: $user to create"))
          userRepositoryActor forward create

        case rejections =>
          log.info(GelfLogger.buildWithRequestId(requestId)
            .info(s"Create - Replying rejections: $user to sender"))
          replyTo ! Left(rejections)
      }
    case Terminated(actorRef) =>
      log.error(GelfLogger.error("Stopping watcher, actor is now dead, re-creating it.",
        Map("internal_operation" -> ACTOR_DEAD)))

      userRepositoryActor = createRepository

    case x: Any => log.warning(GelfLogger.warn(s"Unknown message: $x", Map("internal_operation" -> UNKNOWN_MESSAGE)))
  }

  def createRepository: ActorRef = {
    val ref = repositoryRef.getOrElse(context.actorOf(UserRepositoryActor.props, "user-repository-actor"))
    context.watch(ref)
    ref
  }
}

object UserServiceActor {
  def props: Props = Props(new UserServiceActor())
}
