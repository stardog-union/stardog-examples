CREATE DATABASE IF NOT EXISTS examples;
USE examples;

DROP TABLE IF EXISTS Bands;

CREATE TABLE Bands(id INTEGER,
                   name VARCHAR(20),
                   country VARCHAR(20),
                   year_formed int);

INSERT INTO Bands VALUES(1, 'Aerosmith', 'USA', 1970);
INSERT INTO Bands VALUES(1, 'Errorsmith', 'USA', 2022);

DROP TABLE IF EXISTS Albums;

CREATE TABLE Albums(id INTEGER,
                    band_id INTEGER,
                    title VARCHAR(50),
                    year INTEGER);

INSERT INTO Albums VALUES(1, 1, 'Aerosmith', 1973);
INSERT INTO Albums VALUES(2, 1, 'Pump', 1989);
