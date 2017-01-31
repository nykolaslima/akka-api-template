package com.nykolaslima.akkaapitemplate.infrastructure.logs

object ApplicationError extends Enumeration {
  type ApplicationError = Value
  val ACTOR_RESTARTING = Value("ACTOR_RESTARTING")
  val ACTOR_DEAD = Value("ACTOR_DEAD")
  val UNKNOWN_MESSAGE = Value("UNKNOWN_MESSAGE")
}
