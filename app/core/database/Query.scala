package core.database

import java.sql.Connection

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.language.higherKinds
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

/**
  * Query is just a wrapper around a function `C => A`.
  * The first type parameter C stands for ‘environment’ or 'context'.
  * The second type parameter A is whatever our functions return.
  *
  * This is a lightweight representation of the reader monad.
  *
  * Limitations:
  *  - Make sure that whatever you compute is evaluated eagerly and no longer depends on the resource, e.g
  *    avoid Future or Iterator or lazy value.
  *  - FlatMap is not stack safe
  */
class Query[-C, +A](block: C => A) {

  def map[B](f: A => B): Query[C, B] = {
    Query { c =>
      f(block(c))
    }
  }

  def flatMap[B, D <: C](f: A => Query[D, B]): Query[D, B] = {
    Query { c =>
      f(run(c)).run(c)
    }
  }

  /**
    * Applies the given function `pf` if this query is a `Failure`.
    * If there is no match, or if this query contains a valid result
    * then the new query will contain the same result.
    */
  def recoverWith[B >: A, D <: C](
      pf: PartialFunction[Throwable, Query[D, B]]): Query[D, B] = {
    Query { c =>
      try {
        run(c)
      } catch {
        case NonFatal(e) if pf isDefinedAt e => pf.apply(e).run(c)
      }
    }
  }

  def run(c: C): A = block(c)
}

object Query {
  def apply[C, A](block: C => A) = new Query(block)

  /**
    * `pure` lifts any value into the Query.
    */
  @inline def pure[C, A](a: A): Query[C, A] = Query[C, A](_ => a)

  /**
    * Creates a Query that will fail on execution.
    */
  @inline def failure[C, A](t: => Throwable): Query[C, A] =
    Query[C, A](_ => throw t)

  /**
    * Creates a Query with the specified result or exception.
    */
  def fromTry[C, A](result: Try[A]): Query[C, A] = result match {
    case Success(a) => pure(a)
    case Failure(t) => failure(t)
  }

  /**
    * Transforms a Seq[Query[C, A]] to a Query[C, Seq[A]]
    */
  def sequence[C, A, M[X] <: TraversableOnce[X]](
      elements: M[Query[C, A]]
  )(implicit cbf: CanBuildFrom[M[Query[C, A]], A, M[A]]): Query[C, M[A]] = {
    val queries = elements.foldLeft(
      Query.pure[C, mutable.Builder[A, M[A]]](cbf(elements))) {
      (builderQ, currentQuery) =>
        for {
          builder <- builderQ
          query <- currentQuery
        } yield builder += query
    }

    queries.map(_.result())
  }

  /**
    * Transforms a Option[Query[C, A]] to a Query[C, Option[A]]
    */
  def sequence[C, A](queryOpt: Option[Query[C, A]]): Query[C, Option[A]] = {
    queryOpt match {
      case Some(value) => value.map(Some(_))
      case None        => Query.pure(None)
    }
  }

  /**
    * Transforms a Query[C, Option[A]] to a Query[C, A], returning a Query.failure when option is empty.
    */
  def mapOptionToError[C, A](query: Query[C, Option[A]],
                             exception: Exception): Query[C, A] = {
    query.flatMap {
      case Some(value) => Query.pure(value)
      case None        => Query.failure(exception)
    }
  }
}

package object data {

  /**
    * Type alias where the context is a java.sql.Connection
    */
  type DatabaseQuery[A] = Query[Connection, A]

  object DatabaseQuery {
    def apply[A](block: Connection => A): DatabaseQuery[A] = new Query(block)
  }

}
