package com.nykolaslima.akkaapitemplate.components.user

import com.nykolaslima.akkaapitemplate.infrastructure.generators.user.UserGenerator
import com.nykolaslima.akkaapitemplate.infrastructure.test.UnitSpec
import com.nykolaslima.akkaapitemplate.infrastructure.validation.ValidationRulesUtil._

class UserValidatorSpec extends UnitSpec with UserGenerator {

  "The UserValidator" must {
    "require name" in {
      val user = userGen.sample.get.copy(name = "")
      val rejections = UserValidator.validate(user)

      rejections.size shouldEqual 1
      rejections(0) shouldEqual requiredRejectionFor("user.name")
    }
  }

}
