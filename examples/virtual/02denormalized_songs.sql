CREATE DATABASE IF NOT EXISTS examples;
USE examples;

DROP TABLE IF EXISTS Songs;
DROP TABLE IF EXISTS SongsNormalized;
DROP TABLE IF EXISTS SongWriter;
DROP TABLE IF EXISTS Artists;

CREATE TABLE Songs(id INTEGER,
                   band INTEGER,
                   writer INTEGER,
                   name VARCHAR(20));

CREATE TABLE SongsNormalized(id INTEGER PRIMARY KEY,
                             name VARCHAR(20));

CREATE TABLE SongWriter(song INTEGER,
						artist INTEGER,
                        PRIMARY KEY(song, artist));


CREATE TABLE Artists(id INTEGER PRIMARY KEY,
                     name VARCHAR(20) NOT NULL);

INSERT INTO Songs VALUES(1, 1, 1, 'Mr. Tambourine Man');
INSERT INTO Songs VALUES(1, 2, 1, 'Mr. Tambourine Man');

INSERT INTO SongsNormalized VALUES(1, 'Mr. Tambourine Man');
INSERT INTO SongsNormalized VALUES(2, 'Changes');
INSERT INTO SongsNormalized VALUES(3, 'Changes');
INSERT INTO SongsNormalized VALUES(4, 'Changes');

INSERT INTO SongWriter VALUES(1, 1);
INSERT INTO SongWriter VALUES(2, 3);
INSERT INTO SongWriter VALUES(3, 4);
INSERT INTO SongWriter VALUES(3, 5);
INSERT INTO SongWriter VALUES(3, 6);
INSERT INTO SongWriter VALUES(4, 7);
INSERT INTO SongWriter VALUES(4, 8);
INSERT INTO SongWriter VALUES(4, 9);
INSERT INTO SongWriter VALUES(4, 10);

INSERT INTO Artists VALUES(1, 'Bob Dylan');
INSERT INTO Artists VALUES(2, 'The Byrds');
INSERT INTO Artists VALUES(3, 'David Bowie');
INSERT INTO Artists VALUES(4, 'Trevor Rabin');
INSERT INTO Artists VALUES(5, 'Jon Anderson');
INSERT INTO Artists VALUES(6, 'Alan White');
INSERT INTO Artists VALUES(7, 'Ozzy Osbourne');
INSERT INTO Artists VALUES(8, 'Tony Iommi');
INSERT INTO Artists VALUES(9, 'Geezer Butler');
INSERT INTO Artists VALUES(10, 'Bill Ward');

