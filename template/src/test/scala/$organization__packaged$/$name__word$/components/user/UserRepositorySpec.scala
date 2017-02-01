package $organization$.$name__word$.components.user

import $organization$.$name__word$.infrastructure.generators.user.UserGenerator
import $organization$.$name__word$.infrastructure.persistence.postgres.PostgresDriver.api._
import $organization$.$name__word$.infrastructure.test.IntegrationSpec
import java.util.UUID

class UserRepositorySpec extends IntegrationSpec with UserGenerator {

  "The UserRepository" when {
    "loadById" should {
      "return existing user" in {
        val user = userGen.sample.get
        db.run(UserRepository.table += user)

        whenReady(UserRepository.loadById(user.id.get)) { result =>
          result shouldEqual Some(user)
        }
      }

      "return nonexistent" in {
        whenReady(UserRepository.loadById(UUID.randomUUID())) { result =>
          result shouldEqual None
        }
      }
    }

    "add" should {
      "return added user" in {
        val user = userGen.sample.get
        whenReady(UserRepository.add(user)) { result =>
          result.id should not be empty
          result.name should not be empty
        }
      }
    }
  }

}
