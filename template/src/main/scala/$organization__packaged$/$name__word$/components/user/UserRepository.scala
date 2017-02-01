package $organization$.$name__word$.components.user

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import $organization$.$name__word$.infrastructure.persistence.postgres.DBConnection
import $organization$.$name__word$.infrastructure.persistence.postgres.PostgresDriver.api._
import java.util.UUID

trait UserRepository extends DBConnection {
  val table = TableQuery[UserModel]

  def loadById(id: UUID): Future[Option[User]] = {
    val query = table.filter(_.uuid === id).result.headOption
    run(query)
  }

  def add(user: User): Future[User] = {
    val userToAdd = user.copy(id = Some(UUID.randomUUID))
    val query = table += userToAdd
    run(query).map(_ => userToAdd)
  }
}

private[user] class UserModel(tag: Tag) extends Table[User](tag, "users") {
  def uuid = column[Option[UUID]]("id", O.PrimaryKey)
  def name = column[String]("name")

  def * = (
    uuid,
    name
    ) <> (User.tupled, User.unapply)
}

object UserRepository extends UserRepository
