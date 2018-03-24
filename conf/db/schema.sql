DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;

CREATE TABLE authors (
  id   SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE books (
  id       SERIAL PRIMARY KEY,
  title    TEXT    NOT NULL,
  year     INTEGER NOT NULL,
  authorId INTEGER REFERENCES authors (id)
);
