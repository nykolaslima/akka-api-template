package com.nykolaslima.akkaapitemplate.infrastructure.generators.user

import com.nykolaslima.akkaapitemplate.components.user.User
import com.nykolaslima.akkaapitemplate.infrastructure.generators.GeneratorUtil
import java.util.UUID

trait UserGenerator extends GeneratorUtil {

  val userGen = for {
    id <- some(UUID.randomUUID())
    name <- alphaStr(30)
  } yield User(id, name)

}
