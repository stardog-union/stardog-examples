CREATE DATABASE virtuals;

USE virtuals;

CREATE TABLE Song(id INTEGER NOT NULL,
				  name VARCHAR(30) NOT NULL,
                  band VARCHAR(30),
                  album VARCHAR(30),
                  length INTEGER);

INSERT INTO Song VALUES(1, 'Changes', 'Blank Sabbath', 'Vol. 4', 283);
INSERT INTO Song VALUES(2, 'Changes', 'David Bowie', 'Hunky Dory', 213);
INSERT INTO Song VALUES(3, 'I Want You', 'The Beatles', 'Abbey Road', 467);
INSERT INTO Song VALUES(4, 'I Want You', 'Bob Dylan', 'Blone On Blonde', 187);
INSERT INTO Song VALUES(5, 'I Want You', 'Marvin Gaye', 'I Want You', 274);
INSERT INTO Song VALUES(6, 'Trying to Get to Heaven', 'David Bowie', 'The Secret Songs', 290);
