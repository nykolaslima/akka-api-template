package com.akkaapitemplate.infrastructure.routes

import com.akkaapitemplate.infrastructure.serialization.ApplicationMarshalling
import com.akkaapitemplate.resources.{ErrorResource, RejectionsResource, UserResource}

trait ApplicationRoute extends ApplicationMarshalling with RequestIdDirective {
  implicit val userUnmarshaller = scalaPBFromRequestUnmarshaller(UserResource)
  implicit val rejectionsUnmarshaller = scalaPBFromRequestUnmarshaller(RejectionsResource)
  implicit val errorUnmarshaller = scalaPBFromRequestUnmarshaller(ErrorResource)
}
