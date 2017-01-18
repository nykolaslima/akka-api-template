package com.akkaapitemplate.components.user

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.util.{ByteString, Timeout}
import com.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.akkaapitemplate.infrastructure.persistence.postgres.PostgresDriver.api._
import com.akkaapitemplate.infrastructure.test.RouteSpec
import com.akkaapitemplate.resources.UserResource
import com.google.protobuf.CodedInputStream
import com.trueaccord.scalapb.json.JsonFormat
import java.util.UUID

class UserRouteSpec extends RouteSpec with UserRoute with UserGenerator {

  def actorSystem: ActorSystem = system
  override val timeout = Timeout(5.second)

  "The UserRoute" must {
    "load user by id with json" in {
      val user = userGen.sample.get
      db.run(UserRepository.table += user)

      Get(s"/users/${user.id.get}").addHeader(acceptJson) ~> routes ~> check {
        status shouldEqual OK
        response.entity.dataBytes.map { bytes =>
          val json = bytes.decodeString(ByteString.UTF_8)
          JsonFormat.fromJsonString(json)(UserResource.messageCompanion)
        }
      }
    }

    "load user by id with proto" in {
      val user = userGen.sample.get
      db.run(UserRepository.table += user)

      Get(s"/users/${user.id.get}").addHeader(acceptProto) ~> routes ~> check {
        status shouldEqual OK
        response.entity.dataBytes.map { bytes =>
          UserResource.parseFrom(CodedInputStream.newInstance(bytes.asByteBuffer)) shouldEqual UserResource(user.id.get.toString, user.name)
        }
      }
    }

    "return not found for nonexistent user" in {
      Get(s"/users/${UUID.randomUUID()}") ~> routes ~> check {
        status shouldEqual NotFound
      }
    }
  }

}
