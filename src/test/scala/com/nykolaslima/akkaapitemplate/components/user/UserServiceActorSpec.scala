package com.nykolaslima.akkaapitemplate.components.user

import akka.actor.{Props, Terminated}
import akka.testkit.TestProbe
import com.nykolaslima.akkaapitemplate.components.user.ActorMessages.{Create, LoadById}
import com.nykolaslima.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.nykolaslima.akkaapitemplate.infrastructure.test.ActorSpec
import com.nykolaslima.akkaapitemplate.infrastructure.validation.ValidationRulesUtil._
import java.util.UUID

class UserServiceActorSpec extends ActorSpec with UserGenerator {

  "The UserServiceActor" when {
    "LoadById message" should {
      "forward message to UserRepositoryActor" in {
        val (userRepositoryActor, _, serviceActor) = setUp()
        serviceActor ! LoadById(requestId = UUID.randomUUID(), id = UUID.randomUUID())
        userRepositoryActor.expectMsgType[LoadById]
      }
    }

    "Create message" should {
      "forward message to UserRepositoryActor on validation succeeded" in {
        val (userRepositoryActor, userValidator, serviceActor) = setUp()
        val user = userGen.sample.get
        //val rejections = List(requiredRejectionFor("user.name"))
        (userValidator.validate _).when(user).returns(List())
        serviceActor ! Create(requestId = UUID.randomUUID(), user = user)
        userRepositoryActor.expectMsgType[Create]
      }

      "reply message to sender with validation rejections" in {
        val (_, userValidator, serviceActor) = setUp()
        val senderActor = TestProbe()
        val user = userGen.sample.get
        val rejections = List(requiredRejectionFor("user.name"))
        (userValidator.validate _).when(user).returns(rejections)
        serviceActor ! Create(UUID.randomUUID(), Some(senderActor.ref), user)
        senderActor.expectMsg(Left(rejections))
      }
    }

    "Terminated message" should {
      "re-create UserRepositoryActor" in {
        val (probe, _, serviceActor) = setUp()
        serviceActor ! Terminated(serviceActor)(true, true)
        probe.expectNoMsg
      }
    }

    "Unknown message" should {
      "not be handled" in {
        val (userRepositoryActor, _, serviceActor) = setUp()
        serviceActor ! "unknown message"
        userRepositoryActor.expectNoMsg
      }
    }
  }

  private def setUp() = {
    val userRepositoryActor = TestProbe()
    val userValidator = stub[UserValidator]
    val serviceActor = system.actorOf(Props(new UserServiceActor(Some(userRepositoryActor.ref), userValidator)))

    (userRepositoryActor, userValidator, serviceActor)
  }

}
