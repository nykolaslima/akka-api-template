package $organization$.$name__word$.components.user

import $organization$.$name__word$.infrastructure.generators.user.UserGenerator
import $organization$.$name__word$.infrastructure.test.UnitSpec
import $organization$.$name__word$.infrastructure.validation.ValidationRulesUtil._

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
