package cinema.controllers

import cinema.models.BookCreation
import cinema.persistence.{AuthorPersistence, BookPersistence}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class HomeController(
    cc: ControllerComponents,
    authorPersistence: AuthorPersistence,
    bookPersistence: BookPersistence,
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
    authorPersistence.list().map { authors =>
      Ok(Json.toJson(authors))
    }
  }

  def books = Action.async {
    bookPersistence.list().map { books =>
      Ok(Json.toJson(books))
    }
  }

  def createBook = Action.async(parse.json[BookCreation]) { request =>
    bookPersistence.create(request.body).map { id =>
      Created(Json.obj("id" -> id))
    }
  }
}
