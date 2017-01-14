package br.com.akkaapitemplate.components.user

import scala.concurrent.Future

import br.com.akkaapitemplate.infrastructure.persistence.postgres.DbConnection
import br.com.akkaapitemplate.infrastructure.persistence.postgres.PostgresDriver.api._
import java.util.UUID

object UserRepository extends DbConnection {
  val table = TableQuery[UserModel]

  def loadById(id: UUID): Future[Option[User]] = {
    val query = table.filter(_.uuid === id).result.headOption
    run(query)
  }
}

private class UserModel(tag: Tag) extends Table[User](tag, "users") {
  def uuid = column[Option[UUID]]("uuid", O.PrimaryKey)
  def name = column[String]("name")

  def * = (
    uuid,
    name
    ) <> (User.tupled, User.unapply)
}
