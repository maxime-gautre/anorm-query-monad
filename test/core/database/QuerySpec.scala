package core.database

import scala.util.{Success, Try}

import org.scalatest.Matchers._
import org.scalatest.WordSpec

class QuerySpec extends WordSpec {

  "Query" should {
    "map" in {
      val query = Query[Unit, List[String]](_ => List("a", "b", "c"))
      val result = query
        .map(_.headOption)
        .map(_.map(_.toUpperCase))
        .run(())

      result shouldBe Some("A")
    }

    "flatMap" in {
      case class User(name: String, age: Int)
      val entries: Map[Int, String] =
        Map(1 -> "member", 2 -> "admin", 3 -> "god")
      val foos: Map[String, User] = Map("member" -> User("Tom", 18),
                                        "admin" -> User("Bob", 12),
                                        "god" -> User("Jack", 34))

      def getUser(id: Int): Try[Option[User]] = {
        val query = for {
          roleOpt <- Query((_: Unit) => entries.get(id))
          user <- Query.pure(roleOpt.flatMap(foos.get))
        } yield user

        Try(query.run(()))
      }

      getUser(2) shouldBe Success(Some(User("Bob", 12)))
      getUser(5) shouldBe Success(None)
    }

    "sequence list" in {
      val listOfQueries: List[Query[Unit, String]] =
        List(Query.pure("A"), Query.pure("B"), Query.pure("C"))
      val result = Query.sequence(listOfQueries)
      result.run(()) should contain theSameElementsAs List("A", "B", "C")
    }

    "sequence option" in {
      val optionOfQueries: Option[Query[Unit, String]] = Option(Query.pure("A"))
      val result = Query.sequence(optionOfQueries)
      result.run(()) shouldBe Some("A")
    }
  }
}
