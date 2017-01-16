package com.akkaapitemplate.infrastructure.serialization.json

import com.github.tminglei.slickpg.JsonString
import org.json4s._
import org.json4s.jackson.JsonMethods._

class JsonStringProtocol extends CustomSerializer[JsonString](format => ({
  case JString(str) => JsonString(str)
}, {
  case json: JsonString => parse(json.value)
}))
