package bookstore.persistence

import anorm.{SQL, SqlParser}

import core.database.Query
import bookstore.models.{Book, BookCreation}

object BookPersistence {

  def list() = Query { implicit connection =>
    SQL(
      """
        |SELECT * from books b
        |LEFT JOIN authors a
        |ON b.authorId = a.id
      """.stripMargin
    ).as(Book.bookParser.*)
  }

  def create(bookCreation: BookCreation, authorId: Int): Query[Int] =
    Query { implicit connection =>
      val bookId = SQL(
        s"""
           |INSERT INTO books (title, year, authorId)
           |VALUES ('${bookCreation.title}', ${bookCreation.year}, $authorId)
        """.stripMargin
      ).executeInsert(SqlParser.scalar[Int].single)

      bookId
    }
}
