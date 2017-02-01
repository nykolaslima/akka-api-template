package $organization$.$name__word$.infrastructure.generators.user

import $organization$.$name__word$.components.user.User
import $organization$.$name__word$.infrastructure.generators.GeneratorUtil
import java.util.UUID

trait UserGenerator extends GeneratorUtil {

  val userGen = for {
    id <- some(UUID.randomUUID())
    name <- alphaStr(30)
  } yield User(id, name)

}
