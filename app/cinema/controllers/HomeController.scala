package cinema.controllers

import cinema.models.{AuthorCreation, BookCreation}
import cinema.persistence.{AuthorPersistence, BookPersistence}
import core.database.QueryRunner
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class HomeController(
    cc: ControllerComponents,
    queryRunner: QueryRunner
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def index(path: String) = Action {
    val initData = Json.obj("helloWorld" -> path)
    Ok(Json.toJson(initData))
  }

  def about = Action {
    Ok(sbt.BuildInfo.toString)
  }

  def authors = Action.async {
    queryRunner.run(AuthorPersistence.list()).map { authors =>
      Ok(Json.toJson(authors))
    }
  }

  def books = Action.async {
    queryRunner.run(BookPersistence.list()).map { books =>
      Ok(Json.toJson(books))
    }
  }

  def createBook = Action.async(parse.json[BookCreation]) { request =>
    val query = for {
      authorId <- AuthorPersistence.create(AuthorCreation(request.body.author))
      bookId <- BookPersistence.create(request.body, authorId)
    } yield bookId

    queryRunner.commit(query).map { id =>
      Created(Json.obj("id" -> id))
    }
  }
}
