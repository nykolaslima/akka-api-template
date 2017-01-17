package com.akkaapitemplate.infrastructure.persistence.postgres

import com.akkaapitemplate.infrastructure.config.AppConfig
import slick.driver.PostgresDriver.api._

trait DBConnection {
  lazy val db = Database.forConfig("database.postgres", AppConfig.config)

  def run[T](dBIOAction: DBIOAction[T, NoStream, Nothing]) = db.run(dBIOAction)
}
