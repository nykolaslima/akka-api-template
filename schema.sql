CREATE DATABASE akkaapitemplate;
CREATE USER akkaapitemplate;

\c akkaapitemplate akkaapitemplate;


CREATE TABLE users (
  id UUID NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

GRANT INSERT, UPDATE, DELETE, SELECT on all tables in schema public to akkaapitemplate;
