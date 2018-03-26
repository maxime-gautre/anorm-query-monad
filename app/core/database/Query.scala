package core.database

import java.sql.Connection

import play.api.db.Database

import scala.collection.generic.CanBuildFrom
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class Query[A](val block: Connection => A) {

  def map[B](f: A => B): Query[B] = {
    Query { connection =>
      f(block(connection))
    }
  }

  def flatMap[B](f: A => Query[B]): Query[B] = {
    Query { connection =>
      f(block(connection)).block(connection)
    }
  }

  def zip[B](query: Query[B]): Query[(A, B)] = {
    flatMap { a =>
      query.map { b =>
        a -> b
      }
    }
  }
}

object Query {
  def apply[A](block: Connection => A) = new Query(block)

  @inline def pure[A](a: A) = Query(_ => a)

  @inline def failure[A](exception: => Exception): Query[A] =
    Query(_ => throw exception)

  def sequence[A, M[X] <: TraversableOnce[X]](
      elements: M[Query[A]]
  )(implicit cbf: CanBuildFrom[M[Query[A]], A, M[A]]): Query[M[A]] = {
    val queries = elements.foldLeft(Query.pure(cbf(elements))) {
      (builderQ, currentQuery) =>
        for {
          builder <- builderQ
          query <- currentQuery
        } yield builder += query
    }

    queries.map(_.result())
  }

  def sequence[A](queryOpt: Option[Query[A]]): Query[Option[A]] = {
    queryOpt match {
      case Some(value) => value.map(Some(_))
      case None        => Query.pure(None)
    }
  }
}

class QueryRunner(database: Database)(implicit ec: ExecutionContext) {
  def run[A](query: Query[A]): Future[A] = Future {
    database.withConnection {
      query.block
    }
  }

  def commit[A](query: Query[A]): Future[A] = Future {
    database.withTransaction {
      query.block
    }
  }
}
