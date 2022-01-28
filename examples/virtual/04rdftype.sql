CREATE DATABASE IF NOT EXISTS examples;
USE examples;

DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Actors;

CREATE TABLE Roles(movie_id  INTEGER     NOT NULL,
                   title     VARCHAR(20) NOT NULL,
                   year      VARCHAR(20) NOT NULL,
                   actor_id  INTEGER     NOT NULL,
                   name      VARCHAR(20) NOT NULL,
                   char_name VARCHAR(20) NOT NULL,
                   PRIMARY KEY (movie_id, actor_id));

INSERT INTO Roles VALUES(1, 'The Princess Bride', 1987, 1, 'Robin Wright',      'Buttercup');
INSERT INTO Roles VALUES(2, 'Unbreakable',        2000, 1, 'Robin Wright Penn', 'Audrey Dunn');

CREATE TABLE Actors(id   INTEGER     NOT NULL,
                    name VARCHAR(20) NOT NULL,
                    PRIMARY KEY (id));

INSERT INTO Actors VALUES(1, 'Robin Wright');
