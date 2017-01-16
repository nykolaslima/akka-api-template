package com.akkaapitemplate.infrastructure.persistence.postgres

import slick.driver.PostgresDriver.api._

trait DBConnection {
  lazy val db = Database.forConfig("database.postgres")

  def run[T](dBIOAction: DBIOAction[T, NoStream, Nothing]) = db.run(dBIOAction)
}
