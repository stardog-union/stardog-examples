CREATE DATABASE IF NOT EXISTS examples;
USE examples;

DROP TABLE IF EXISTS Movies;
DROP TABLE IF EXISTS Actors;

CREATE TABLE Movies(id    INTEGER     NOT NULL,
                    title VARCHAR(20) NOT NULL,
                    year  VARCHAR(20) NOT NULL,
                    PRIMARY KEY (id));

INSERT INTO Movies VALUES(1, 'The Princess Bride', 1987);
INSERT INTO Movies VALUES(2, 'Unbreakable',        2000);
INSERT INTO Movies VALUES(3, 'Forest Gump',        1994);

CREATE TABLE Actors(id   INTEGER     NOT NULL,
                    name VARCHAR(20) NOT NULL,
                    PRIMARY KEY (id));

INSERT INTO Actors VALUES(1, 'Robin Wright');
INSERT INTO Actors VALUES(2, 'Tom Hanks');
