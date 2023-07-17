-- We don´t want to reset the containerized postgresql db as it is a demo of a pseudo production environment
-- where we want to experience persistence of data between sessions
-- DROP TABLE IF EXISTS book;
CREATE TABLE if not exists book (
-- CREATE TABLE if book (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    author              varchar(30) NOT NULL,
    isbn                varchar(13) UNIQUE NOT NULL,
    price               float8 NOT NULL CONSTRAINT positive_book_price CHECK (price > 0),
    title               varchar(50) NOT NULL,
    created_date        timestamp NOT NULL,
    last_modified_date  timestamp NOT NULL,
    version             integer NOT NULL
);