package com.nykolaslima.akkaapitemplate.components.swagger

import scala.util.{Failure, Success}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshaller._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.google.protobuf.Descriptors.Descriptor
import com.nykolaslima.akkaapitemplate.resources.{ErrorResource, RejectionsResource, UserResource}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import spray.json.DefaultJsonProtocol

class SwaggerRoute extends Json4sSupport with DefaultJsonProtocol with SprayJsonSupport {

  implicit val formats = DefaultFormats
  implicit val serialization = jackson.Serialization

  val descriptors: List[Descriptor] = List(
    UserResource,
    RejectionsResource,
    RejectionsResource,
    ErrorResource
  ).map(_.javaDescriptor)

  def routes = get {
    pathPrefix("api-docs") {
      pathEnd {
        redirect("/", MovedPermanently)
      } ~
        pathSingleSlash {
          getFromResource("public/swagger/index.html")
        } ~ getFromResourceDirectory("public/swagger/") ~
        path("swagger.json") {
          onComplete(SwaggerDocs(("/api-docs/", descriptors)).build()) {
            case Success(result) => complete(result)
            case Failure(failure) => complete((InternalServerError, failure))
          }
        }
    }
  }

}
