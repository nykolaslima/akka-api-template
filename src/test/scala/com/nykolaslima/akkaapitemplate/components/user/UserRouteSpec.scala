package com.nykolaslima.akkaapitemplate.components.user

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.util.Timeout
import com.nykolaslima.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.nykolaslima.akkaapitemplate.infrastructure.persistence.postgres.PostgresDriver.api._
import com.nykolaslima.akkaapitemplate.infrastructure.test.RouteSpec
import com.nykolaslima.akkaapitemplate.infrastructure.validation.ValidationRulesUtil._
import com.nykolaslima.akkaapitemplate.resources.{RejectionsResource, UserResource}
import java.util.UUID

class UserRouteSpec extends RouteSpec with UserRoute with UserGenerator {

  def actorSystem: ActorSystem = system
  override val timeout = Timeout(5.second)

  "The UserRoute" when {
    "load user by id" should {
      "return existing user with json" in {
        val user = userGen.sample.get
        db.run(UserRepository.table += user)

        Get(s"/users/${user.id.get}").addHeader(acceptJson) ~> routes ~> check {
          status shouldEqual OK
          responseAs[UserResource] shouldEqual UserResource(user.id.get.toString, user.name)
        }
      }

      "return existing user with proto" in {
        val user = userGen.sample.get
        db.run(UserRepository.table += user)

        Get(s"/users/${user.id.get}").addHeader(acceptProto) ~> routes ~> check {
          status shouldEqual OK
          responseAs[UserResource] shouldEqual UserResource(user.id.get.toString, user.name)
        }
      }

      "return not found for nonexistent user" in {
        Get(s"/users/${UUID.randomUUID()}") ~> routes ~> check {
          status shouldEqual NotFound
        }
      }
    }

    "add user" should {
      "add user with json" in {
        val user = userGen.sample.get
        val resource = UserResource(user.id.get.toString, user.name)

        Post("/users", resource).addHeader(acceptJson).addHeader(contentJson) ~> routes ~> check {
          status shouldEqual Created
          val response = responseAs[UserResource]
          response.id should not be empty
          response.name shouldEqual user.name
        }
      }

      "add user with proto" in {
        val user = userGen.sample.get
        val resource = UserResource(user.id.get.toString, user.name)

        Post("/users", resource).addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual Created
          val response = responseAs[UserResource]
          response.id should not be empty
          response.name shouldEqual user.name
        }
      }

      "return rejections when missing required field" in {
        val user = userGen.sample.get.copy(name = "")
        val resource = UserResource(user.id.get.toString, user.name)

        Post("/users", resource).addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual BadRequest
          responseAs[RejectionsResource].errors shouldEqual Vector(requiredRejectionResourceFor("user.name"))
        }
      }
    }
  }

}
