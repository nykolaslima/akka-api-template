package $organization$.$name__word$.infrastructure.test

import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.RouteTestTimeout
import $organization$.$name__word$.infrastructure.persistence.postgres.PostgresDriver.api._
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

trait IntegrationSpec extends UnitSpec with ScalaFutures {
  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(5.seconds)
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(10000, Millis))

  val config = ConfigFactory.load("test")
  val db = Database.forConfig("database.postgres", config)

  before {
    Await.result(db.run(sqlu"delete from users;"), 5.seconds)
  }
}
