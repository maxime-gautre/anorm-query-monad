package bookstore.persistence

import anorm.{SQL, SqlParser}

import core.database.data.DatabaseQuery
import bookstore.models.{Author, AuthorCreation}

object AuthorPersistence {

  def list(): DatabaseQuery[List[Author]] = DatabaseQuery {
    implicit connection =>
      SQL(
        """
        |SELECT * from authors
      """.stripMargin
      ).as(Author.authorParser.*)
  }

  def create(authorCreation: AuthorCreation): DatabaseQuery[Int] =
    DatabaseQuery { implicit connection =>
      val authorId = SQL(
        s"""
         |INSERT INTO authors (name) VALUES ('${authorCreation.name}')
         |ON CONFLICT (name) DO UPDATE SET name = '${authorCreation.name}'
      """.stripMargin
      ).executeInsert(SqlParser.scalar[Int].single)

      authorId
    }
}
