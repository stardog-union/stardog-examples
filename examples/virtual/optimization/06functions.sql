CREATE DATABASE IF NOT EXISTS examples;
USE examples;

DROP TABLE IF EXISTS Remakes;

CREATE TABLE Remakes(id INTEGER,
                     title        VARCHAR(40) NOT NULL,
                     release_year INTEGER     NOT NULL,
                     PRIMARY KEY (id),
                     KEY (title, release_year));

INSERT INTO Remakes VALUES(1, '3:10 to Yuma', 1957);
INSERT INTO Remakes VALUES(2, '3:10 to Yuma', 2007);
INSERT INTO Remakes VALUES(3, 'A Star Is Born', 1937);
INSERT INTO Remakes VALUES(4, 'A Star Is Born', 1976);
INSERT INTO Remakes VALUES(5, 'A Star Is Born', 2018);
INSERT INTO Remakes VALUES(6, 'Ocean''s Eleven', 1960);
INSERT INTO Remakes VALUES(7, 'Ocean''s Eleven', 2001);
INSERT INTO Remakes VALUES(8, 'True Grit', 1969);
INSERT INTO Remakes VALUES(9, 'True Grit', 2010);

SELECT `title`, `release_year`, CONCAT(CONCAT(CONCAT(`title`, ' ('), CAST(`release_year` AS CHAR)), ')') AS `label`
FROM `examples`.`Remakes`
WHERE `id` = 5;

SELECT `id`, CONCAT(CONCAT(CONCAT(`title`, ' ('), CAST(`release_year` AS CHAR)), ')') AS `label`
FROM `examples`.`Remakes`
WHERE `title` = 'A Star is Born' AND `release_year` = 2018;

SELECT `id`, CONCAT(CONCAT(CONCAT(`title`, ' ('), CAST(`release_year` AS CHAR)), ')') AS `label`
FROM `examples`.`Remakes`
WHERE `title` = 'A Star is Born' AND `release_year` = 2018;
