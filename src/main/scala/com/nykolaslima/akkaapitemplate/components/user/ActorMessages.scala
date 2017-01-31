package com.nykolaslima.akkaapitemplate.components.user

import akka.actor.ActorRef
import java.util.UUID

object ActorMessages {
  trait HttpMessage {
    val requestId: UUID
  }

  case class LoadById(override val requestId: UUID, originalSender: Option[ActorRef] = None, id: UUID) extends HttpMessage
  case class Create(override val requestId: UUID, originalSender: Option[ActorRef] = None, user: User) extends HttpMessage
  case class Fail(cause: Throwable)
}
