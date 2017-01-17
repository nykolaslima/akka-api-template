package com.akkaapitemplate.components.user

import com.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.akkaapitemplate.infrastructure.persistence.postgres.PostgresDriver.api._
import com.akkaapitemplate.infrastructure.test.IntegrationSpec
import org.scalatest.concurrent.ScalaFutures._

class UserRepositorySpec extends IntegrationSpec with UserGenerator {

  "The UserRepository" must {
    "load user by id" in {
      val user = userGen.sample.get
      db.run(UserRepository.table += user)

      whenReady(UserRepository.loadById(user.id.get)) { result =>
        result shouldEqual Some(user)
      }
    }
  }

}
