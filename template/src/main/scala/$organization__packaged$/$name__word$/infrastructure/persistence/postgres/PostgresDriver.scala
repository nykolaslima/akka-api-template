package $organization$.$name__word$.infrastructure.persistence.postgres

import com.github.tminglei.slickpg.{ExPostgresDriver, PgArraySupport, PgEnumSupport, PgJsonSupport}

trait PostgresDriver extends ExPostgresDriver with PgArraySupport with PgJsonSupport with PgEnumSupport {
  override def pgjson: String = "jsonb"
  override val api = new API with ArrayImplicits with JsonImplicits {}
}

object PostgresDriver extends PostgresDriver
