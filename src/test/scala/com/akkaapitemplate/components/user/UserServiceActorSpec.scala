package com.akkaapitemplate.components.user

import akka.actor.{Props, Terminated}
import akka.testkit.TestProbe
import com.akkaapitemplate.components.user.ActorMessages.LoadById
import com.akkaapitemplate.infrastructure.test.ActorSpec
import java.util.UUID

class UserServiceActorSpec extends ActorSpec {

  "The UserServiceActor" must {
    "forward LoadById message to UserRepositoryActor" in {
      val (probe, serviceActor) = setUp()
      serviceActor ! LoadById(requestId = UUID.randomUUID(), id = UUID.randomUUID())
      probe.expectMsgType[LoadById]
    }

    "re-create UserRepositoryActor on Terminated message" in {
      val (probe, serviceActor) = setUp()
      serviceActor ! Terminated(serviceActor)(true, true)
      probe.expectNoMsg
    }

    "not handle unknown message" in {
      val (probe, serviceActor) = setUp()
      serviceActor ! "unknown message"
      probe.expectNoMsg
    }
  }

  private def setUp() = {
    val probe = TestProbe()
    val serviceActor = system.actorOf(Props(new UserServiceActor(Some(probe.ref))))

    (probe, serviceActor)
  }

}
