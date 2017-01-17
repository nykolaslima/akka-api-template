package com.akkaapitemplate.components.user

import scala.concurrent.Future

import akka.actor.Props
import akka.actor.Status.Failure
import akka.testkit.{TestActorRef, TestKit}
import com.akkaapitemplate.components.user.ActorMessages.{Fail, LoadById}
import com.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.akkaapitemplate.infrastructure.test.ActorSpec
import java.util.UUID

class UserRepositoryActorSpec extends ActorSpec with UserGenerator {

  override def afterAll () = TestKit.shutdownActorSystem(system)

  "The UserRepositoryActor" must {
    "load user from repository on LoadById message" in {
      val (userRepository, userRepositoryActor) = setUp()
      val user = userGen.sample.get
      (userRepository.loadById _).when(user.id.get).returns(Future.successful(Some(user)))

      userRepositoryActor ! LoadById(requestId = UUID.randomUUID(), id = user.id.get)

      expectMsg(Some(user))
    }

    "send Failure message on repository load fail" in {
      val (userRepository, userRepositoryActor) = setUp()
      val id = UUID.randomUUID()
      val exception = new RuntimeException
      (userRepository.loadById _).when(id).returns(Future.failed(exception))

      userRepositoryActor ! LoadById(requestId = UUID.randomUUID(), id = id)

      expectMsg(Failure(exception))
    }

    "crash on Fail message" in {
      val (userRepositoryMock, _) = setUp()
      val actorRef = TestActorRef(new UserRepositoryActor(userRepositoryMock))
      val exception = new RuntimeException
      intercept[RuntimeException] {
        actorRef.receive(Fail(exception))
      }
    }

    "not handle unknown message" in {
      val (_, userRepositoryActor) = setUp()
      userRepositoryActor ! "unknown message"
      expectNoMsg
    }
  }

  private def setUp() = {
    val userRepositoryMock = stub[UserRepository]
    val userRepositoryActor = system.actorOf(Props(new UserRepositoryActor(userRepositoryMock)))

    (userRepositoryMock, userRepositoryActor)
  }

}
