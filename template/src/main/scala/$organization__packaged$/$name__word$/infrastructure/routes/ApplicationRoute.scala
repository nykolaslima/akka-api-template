package $organization$.$name__word$.infrastructure.routes

import $organization$.$name__word$.infrastructure.serialization.ApplicationMarshalling
import $organization$.$name__word$.resources.{ErrorResource, RejectionsResource, UserResource}

trait ApplicationRoute extends ApplicationMarshalling with RequestIdDirective {
  implicit val userUnmarshaller = scalaPBFromRequestUnmarshaller(UserResource)
  implicit val rejectionsUnmarshaller = scalaPBFromRequestUnmarshaller(RejectionsResource)
  implicit val errorUnmarshaller = scalaPBFromRequestUnmarshaller(ErrorResource)
}
