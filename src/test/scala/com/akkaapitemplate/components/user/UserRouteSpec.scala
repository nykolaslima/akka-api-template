package com.akkaapitemplate.components.user

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.util.Timeout
import com.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.akkaapitemplate.infrastructure.persistence.postgres.PostgresDriver.api._
import com.akkaapitemplate.infrastructure.serialization.json.MyJsonProtocol._
import com.akkaapitemplate.infrastructure.test.RouteSpec
import java.util.UUID

class UserRouteSpec extends RouteSpec with UserRoute with UserGenerator {

  def actorSystem: ActorSystem = system
  override val timeout = Timeout(5.second)

  "The UserRoute" must {
    "load user by id" in {
      val user = userGen.sample.get
      db.run(UserRepository.table += user)

      Get(s"/users/${user.id.get}") ~> routes ~> check {
        status shouldEqual OK
        responseAs[User] shouldEqual user
      }
    }

    "return not found for nonexistent user" in {
      Get(s"/users/${UUID.randomUUID()}") ~> routes ~> check {
        status shouldEqual NotFound
      }
    }
  }

}
