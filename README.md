# Anorm Query Monad 

This project aims to provide a lightweight `Query` type to perform queries in Anorm. 
We have a sample play application to show how to use it in a real context. 
This app uses compile time dependency injection.

This example uses the following:

- Play! Framework v2.6.11 as a web framework
- Anorm as data access layer
- PostgreSql as a database embedded in docker

## Setup and run

- Deploy the postgres database in docker
```
docker stack deploy -c stack.yml postgres
```

- Add the database [schema](conf/db/schema.sql).
- Run the play application
```
sbt run
```
This application exposes a REST API. You can open a browser and go to localhost:9000.

## Core concepts

### The Problem

The problem arises when working with relational databases in a web app. 
The following are desirable:

- Operations should run in a transaction/connection/context
- We want to avoid writing long functions with multiple queries targeting different tables in the same scope
- Easily compose queries

### Solution

This project introduces a new data type [Query](app/core/database/Query.scala).
Query treats functions as value in the context of a database connection. 
```
val query: Query[Book] = for {
  authorId <- Query(connection => SQL("SELECT id from authors where name = $name").as(SqlParser.scalar[Int].single)
  book <- Query(connection => SQL("SELECT * from books where authorId = $authorId").as(SqlParser.scalar[Book].single)
} yield book
```

Then you can run the query:
```
val queryRunner = new QueryRunner(database)
...
queryRunner.run(query)
```

Everything is in for-comprehensions which makes it easier to compose. 
The value returned from the for-comprehension 
is a Query[A], so nothing happens until that Query is run in `QueryRunner`.

Any functions building on the Database will return Query[A] 
thus making it very obvious what the context of those functions are.

You can easily isolate queries in their own object/class/functions 
which improves readability. 


## To go further:

`Query` is a lightweight version of 
the reader monad that we can find in cats or scalaz.

- [Cats version](https://typelevel.org/cats/datatypes/kleisli.html)
- [Scalaz version](http://eed3si9n.com/learning-scalaz/Reader.html)
- [Using Cat Data Reader Monad](https://medium.com/@AyacheKhettar/using-cat-data-reader-monad-d70269fc451f)
- [A Small (Real) Example of the Reader and Writer Monads](https://underscore.io/blog/posts/2014/07/27/readerwriterstate.html)



