package cinema.persistence

import anorm.SQL
import cinema.models.Author
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

class AuthorPersistence(database: Database) {

  def list()(implicit ec: ExecutionContext): Future[List[Author]] = {
    Future {
      database.withConnection { implicit c =>
        SQL(
          """
            |SELECT * from authors
          """.stripMargin
        ).as(Author.authorParser.*)
      }
    }
  }
}
