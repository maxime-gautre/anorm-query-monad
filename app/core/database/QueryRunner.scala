package core.database

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

import play.api.db.{Database, NamedDatabase}

import core.database.data.DatabaseQuery

trait QueryRunner {
  def run[A](query: DatabaseQuery[A]): Future[A]

  def commit[A](query: DatabaseQuery[A]): Future[A]
}

class DatabaseQueryRunner @Inject()(
  @NamedDatabase("actionDb") database: Database
)(implicit ec: ExecutionContext) extends QueryRunner {

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
