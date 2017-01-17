package com.akkaapitemplate.infrastructure.generators.user

import com.akkaapitemplate.components.user.User
import com.akkaapitemplate.infrastructure.generators.GeneratorUtil
import java.util.UUID

trait UserGenerator extends GeneratorUtil {

  val userGen = for {
    id <- some(UUID.randomUUID())
    name <- alphaStr(30)
  } yield User(id, name)

}
