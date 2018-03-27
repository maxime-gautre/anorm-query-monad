package bookstore.models

import anorm.Macro
import play.api.libs.json.Json

case class Author(id: Int, name: String)

object Author {
  implicit val jsonWriter = Json.writes[Author]

  implicit val authorParser = Macro.namedParser[Author]
}

case class AuthorCreation(name: String)

object AuthorCreation {
  implicit val jsonReader = Json.reads[AuthorCreation]
}
