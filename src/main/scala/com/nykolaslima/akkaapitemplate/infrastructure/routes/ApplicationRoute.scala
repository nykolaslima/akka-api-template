package com.nykolaslima.akkaapitemplate.infrastructure.routes

import com.nykolaslima.akkaapitemplate.infrastructure.serialization.ApplicationMarshalling
import com.nykolaslima.akkaapitemplate.resources.{ErrorResource, RejectionsResource, UserResource}

trait ApplicationRoute extends ApplicationMarshalling with RequestIdDirective {
  implicit val userUnmarshaller = scalaPBFromRequestUnmarshaller(UserResource)
  implicit val rejectionsUnmarshaller = scalaPBFromRequestUnmarshaller(RejectionsResource)
  implicit val errorUnmarshaller = scalaPBFromRequestUnmarshaller(ErrorResource)
}
