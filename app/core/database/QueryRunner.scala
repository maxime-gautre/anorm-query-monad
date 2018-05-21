package core.database

import java.sql.Connection

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

import play.api.db.{Database, NamedDatabase}

import core.database.data.DatabaseQuery

trait QueryRunner[C] {
  def run[A](query: Query[C, A]): Future[A]

  def commit[A](query: Query[C, A]): Future[A]
}

class DatabaseQueryRunner @Inject()(
    @NamedDatabase("actionDb") database: Database
)(implicit ec: ExecutionContext)
    extends QueryRunner[Connection] {

  def run[A](query: DatabaseQuery[A]): Future[A] = Future {
    database.withConnection { c =>
      query.run(c)
    }
  }

  def commit[A](query: DatabaseQuery[A]): Future[A] = Future {
    database.withTransaction { c =>
      query.run(c)
    }
  }
}
