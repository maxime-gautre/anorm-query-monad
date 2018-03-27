package bookstore.persistence

import anorm.{SQL, SqlParser}

import core.database.Query
import bookstore.models.{Author, AuthorCreation}

object AuthorPersistence {

  def list(): Query[List[Author]] = Query { implicit connection =>
    SQL(
      """
        |SELECT * from authors
      """.stripMargin
    ).as(Author.authorParser.*)
  }

  def create(authorCreation: AuthorCreation): Query[Int] = Query {
    implicit connection =>
      val authorId = SQL(
        s"""
         |INSERT INTO authors (name) VALUES ('${authorCreation.name}')
         |ON CONFLICT (name) DO UPDATE SET name = '${authorCreation.name}'
      """.stripMargin
      ).executeInsert(SqlParser.scalar[Int].single)

      authorId
  }
}
