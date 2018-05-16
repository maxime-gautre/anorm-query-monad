package bookstore.persistence

import anorm.{SQL, SqlParser}

import core.database.data.DatabaseQuery
import bookstore.models.{Book, BookCreation}

object BookPersistence {

  def list() = DatabaseQuery { implicit connection =>
    SQL(
      """
        |SELECT * from books b
        |LEFT JOIN authors a
        |ON b.authorId = a.id
      """.stripMargin
    ).as(Book.bookParser.*)
  }

  def create(bookCreation: BookCreation, authorId: Int): DatabaseQuery[Int] =
    DatabaseQuery { implicit connection =>
      val bookId = SQL(
        s"""
           |INSERT INTO books (title, year, authorId)
           |VALUES ('${bookCreation.title}', ${bookCreation.year}, $authorId)
        """.stripMargin
      ).executeInsert(SqlParser.scalar[Int].single)

      bookId
    }
}
