package bookstore.controllers

import java.sql.Connection

import scala.concurrent.ExecutionContext

import play.api.libs.json.Json
import play.api.mvc._

import core.database.QueryRunner
import bookstore.models.{AuthorCreation, BookCreation}
import bookstore.persistence.{AuthorPersistence, BookPersistence}

class HomeController(
    cc: ControllerComponents,
    queryRunner: QueryRunner[Connection]
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
