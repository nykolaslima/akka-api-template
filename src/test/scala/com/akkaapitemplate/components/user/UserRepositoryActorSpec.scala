package com.akkaapitemplate.components.user

import scala.concurrent.Future

import akka.actor.Props
import akka.actor.Status.Failure
import akka.testkit.{TestActorRef, TestKit}
import com.akkaapitemplate.components.user.ActorMessages.{Create, Fail, LoadById}
import com.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.akkaapitemplate.infrastructure.test.ActorSpec
import java.util.UUID

class UserRepositoryActorSpec extends ActorSpec with UserGenerator {

  "The UserRepositoryActor" when {
    "LoadById message" should {
      "load user from repository" in {
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
    }

    "Create message" should {
      "add user in repository" in {
        val (userRepository, userRepositoryActor) = setUp()
        val user = userGen.sample.get
        val addedUser = user.copy(id = Some(UUID.randomUUID()))

        (userRepository.add _).when(user).returns(Future.successful(addedUser))
        userRepositoryActor ! Create(requestId = UUID.randomUUID(), user = user)

        expectMsg(Right(addedUser))
      }

      "send Failure message on repository add fail" in {
        val (userRepository, userRepositoryActor) = setUp()
        val user = userGen.sample.get
        val exception = new RuntimeException
        (userRepository.add _).when(user).returns(Future.failed(exception))

        userRepositoryActor ! Create(requestId = UUID.randomUUID(), user = user)

        expectMsg(Failure(exception))
      }
    }

    "Fail message" should {
      "crash" in {
        val (userRepositoryMock, _) = setUp()
        val actorRef = TestActorRef(new UserRepositoryActor(userRepositoryMock))
        val exception = new RuntimeException
        intercept[RuntimeException] {
          actorRef.receive(Fail(exception))
        }
      }
    }

    "Unknown message" should {
      "not be handled" in {
        val (_, userRepositoryActor) = setUp()
        userRepositoryActor ! "unknown message"
        expectNoMsg
      }
    }
  }

  private def setUp() = {
    val userRepositoryMock = stub[UserRepository]
    val userRepositoryActor = system.actorOf(Props(new UserRepositoryActor(userRepositoryMock)))

    (userRepositoryMock, userRepositoryActor)
  }

}
