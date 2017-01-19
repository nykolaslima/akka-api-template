package com.akkaapitemplate.components.swagger

import com.akkaapitemplate.infrastructure.config.AppConfig._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.parsing.json.JSON

import com.google.protobuf.Descriptors.{Descriptor, EnumValueDescriptor, FieldDescriptor}
import com.typesafe.config.ConfigFactory

/**
  * Simple library that builds swagger docs based on Swagger's specification partials
  * and **Google's Protobuf**.
  *
  * ### Usage:
  *
  * Your Protobuf generated resources:
  *
  *   val descriptors = List(
  *     AddressResource,
  *     RejectionResource,
  *     GeoLocationResource,
  *     GeocodingResultResource
  *   ).map(_.descriptor)
  *
  * For configuration matter you should pass a config tuple specifying your resource folder
  * that contains the swagger partials, and a list of protobuf `descriptors`:
  *
  *   SwaggerDocs(("/api-docs/", descriptors)).build())
  *
  * It gives you ready swagger specification ready to be serialized.
  *
  */
object SwaggerDocs {

  private var _docsPath: String = _
  private var _resources: List[Descriptor] = _

  /**
    * @param config Docs path and protobuf descriptors
    */
  def apply(config: (String, List[Descriptor])) = {
    _docsPath = config._1
    _resources = config._2

    this
  }

  def build(): Future[Map[String, Any]] = {
    for {
      c <- core()
      d <- definitions()
      dc <- dynamicConfigs()
    } yield c ++ d ++ dc
  }

  private def core(): Future[Map[String, Any]] = Future {
    def parseDocPartial(content: String) = JSON.parseFull(content).get.asInstanceOf[Map[String, Any]]
    def concatDocsPartials =
      (a: Map[String, Any], b: Map[String, Any]) => b.flatMap(m => Map(m._1 -> m._2)) ++ a

    List("config", "info", "paths", "tags")
      .map(partial => getClass.getResourceAsStream(_docsPath + partial + ".json"))
      .map(stream => parseDocPartial(Source.fromInputStream(stream).mkString))
      .foldLeft(Map[String, Any]())(concatDocsPartials)
  }

  private def definitions(): Future[Map[String, Any]] = Future {
    def fieldDetails(descriptor: FieldDescriptor) = {
      def format(t: Either[FieldDescriptor.Type, FieldDescriptor.JavaType]) = {
        val formatOrType = t match {
          case Left(t) => FieldDescriptor.Type.valueOf(t.toProto)
          case Right(t) => FieldDescriptor.JavaType.valueOf(t.toString)
        }

        formatOrType.toString.toLowerCase()
      }

      /**
        * Map protobuf types against Swagger specification. More details: http://swagger.io/specification/
        */
      def buildAttribute(attributeType: String) = {
        attributeType match {
          case "MESSAGE" =>
            Map("$ref" -> s"#/definitions/${descriptor.getMessageType.getName}")
          case "ENUM" =>
            val values = descriptor.getEnumType.getValues
              .toArray.asInstanceOf[Array[EnumValueDescriptor]].map(_.getName)

            Map("type" -> "string", "enum" -> values)
          case "STRING" =>
            Map("type" -> format(Left(descriptor.getType)))
          case "DOUBLE" =>
            Map("type" -> "number", "format" -> format(Left(descriptor.getType)))
          case "INT32" | "INT64" =>
            Map("type" -> "integer", "format" -> format(Left(descriptor.getType)))
          case _ =>
            Map(
              "type" -> format(Left(descriptor.getType)),
              "format" -> format(Right(descriptor.getType.getJavaType))
            )
        }
      }

      val attribute = buildAttribute(descriptor.getType.toString())

      if (descriptor.isRepeated) Map("type" -> "array", "items" -> attribute)
      else attribute
    }

    def fields(descriptor: Descriptor) = {
      descriptor.getFields.toArray.asInstanceOf[Array[FieldDescriptor]]
        .foldLeft(Map[String, Any]()) { (a, b) => Map(b.getName -> fieldDetails(b)) ++ a }
    }

    Map(
      "definitions" -> _resources.flatMap { resource =>
        Map(resource.getName -> Map(
          "type" -> "object",
          "properties" -> fields(resource)
        ))
      }.toMap
    )
  }

  private def dynamicConfigs(): Future[Map[String, Any]] = Future {
    Map(
      "host" -> config.getString("swagger.host")
    )
  }
}
