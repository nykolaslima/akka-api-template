package $organization$.$name__word$.infrastructure.persistence.postgres

import $organization$.$name__word$.infrastructure.config.AppConfig
import slick.driver.PostgresDriver.api._

trait DBConnection {
  lazy val db = Database.forConfig("database.postgres", AppConfig.config)

  def run[T](dBIOAction: DBIOAction[T, NoStream, Nothing]) = db.run(dBIOAction)
}
