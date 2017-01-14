package br.com.akkaapitemplate.components.user

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.event.LoggingReceive
import br.com.akkaapitemplate.components.user.ActorMessages.LoadById
import br.com.akkaapitemplate.infrastructure.logs.GelfLogger
import br.com.akkaapitemplate.infrastructure.logs.ApplicationError._

class UserServiceActor(repositoryRef: Option[ActorRef] = None) extends Actor with ActorLogging {
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: java.sql.SQLTimeoutException => Restart
    case _: org.postgresql.util.PSQLException => Stop
    case _: Exception => Restart
  }

  var userRepositoryActor: ActorRef = createRepository

  def receive = LoggingReceive {
    case loadById @ LoadById(requestId, _, id) =>
      log.info(GelfLogger.buildWithRequestId(requestId).info(s"LoadById - Forwarding user id: $id to load"))
      userRepositoryActor forward loadById
    case Terminated(actorRef) =>
      log.error(GelfLogger.error("Stopping watcher, actor is now dead, re-creating it.", Map("internal_operation" -> ACTOR_DEAD)))
      userRepositoryActor = createRepository
    case x => log.warning(GelfLogger.warn(s"Unknown message: $x", Map("internal_operation" -> UNKNOWN_MESSAGE)))
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
