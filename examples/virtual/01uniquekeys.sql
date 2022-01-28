CREATE DATABASE examples;

USE examples;

CREATE TABLE Bands(id INTEGER, -- note no primary key
                   name VARCHAR(20),
                   country VARCHAR(20),
                   year_formed int);

INSERT INTO Bands VALUES(1, 'Aerosmith', 'USA', 1970);
INSERT INTO Bands VALUES(1, 'Errorsmith', 'USA', 2022);

DELETE FROM Bands WHERE id=1 AND year_formed=2022;

