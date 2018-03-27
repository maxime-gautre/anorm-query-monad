package bookstore.models

import anorm.Macro
import play.api.libs.json.Json

case class Book(
    id: Int,
    title: String,
    year: Int,
    author: Author
)

object Book {
  implicit val jsonWriter = Json.writes[Book]

  implicit val bookParser = Macro.namedParser[Book]
}

case class BookCreation(
    title: String,
    year: Int,
    author: String
)

object BookCreation {
  implicit val jsonReader = Json.reads[BookCreation]
}
