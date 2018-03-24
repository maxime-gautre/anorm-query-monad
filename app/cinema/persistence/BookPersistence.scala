package cinema.persistence

import anorm.{SQL, SqlParser}
import cinema.models.{Book, BookCreation}
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

class BookPersistence(database: Database) {

  def list()(implicit ec: ExecutionContext) = Future {
    database.withConnection { implicit connection =>
      SQL(
        """
          |SELECT * from books b
          |LEFT JOIN authors a
          |ON b.authorId = a.id
        """.stripMargin
      ).as(Book.bookParser.*)
    }
  }

  def create(bookCreation: BookCreation)(
      implicit ec: ExecutionContext): Future[Int] = Future {
    database.withTransaction { implicit connection =>
      val authorId = SQL(
        s"""
           |INSERT INTO authors (name) VALUES ('${bookCreation.author}')
           |ON CONFLICT (name) DO UPDATE SET name = '${bookCreation.author}'
      """.stripMargin
      ).executeInsert(SqlParser.scalar[Int].single)

      val bookId = SQL(
        s"""
          |INSERT INTO books (title, year, authorId)
          |VALUES ('${bookCreation.title}', ${bookCreation.year}, $authorId)
        """.stripMargin
      ).executeInsert(SqlParser.scalar[Int].single)

      bookId
    }
  }
}
