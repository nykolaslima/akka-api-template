package com.nykolaslima.akkaapitemplate.infrastructure.config

import com.typesafe.config.{Config, ConfigFactory}

object AppConfig {
  def fromSystem(prop: String, default: String) = {
    Option(System.getenv(prop))
      .getOrElse(Option(System.getProperty(prop))
        .getOrElse(default))
  }

  val environment = fromSystem("environment", "test")
  val config: Config = ConfigFactory.load(environment.toLowerCase)
}
