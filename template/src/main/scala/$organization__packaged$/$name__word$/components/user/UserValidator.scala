package $organization$.$name__word$.components.user

import $organization$.$name__word$.infrastructure.validation.{Rejection, Rule, ValidationRules, Validator}

trait UserValidator extends ValidationRules {
  def validate(user: User): List[Rejection] = {
    val rules: List[Rule[User]] = List(
      Rule(notEmpty(user.name, "user.name"))
    )

    Validator.validate(user, rules)
  }
}

object UserValidator extends UserValidator
