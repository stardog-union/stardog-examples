CREATE DATABASE listeners;

USE listeners;

CREATE TABLE Bands(name VARCHAR(20) NOT NULL PRIMARY KEY,
                  country VARCHAR(20),
                  year_formed int);

CREATE TABLE Songs(name VARCHAR(30) NOT NULL PRIMARY KEY,
                   writer VARCHAR(70));

CREATE TABLE Recordings(band VARCHAR(20) NOT NULL REFERENCES Bands(name),
                        song VARCHAR(30) NOT NULL REFERENCES Songs(name),
                        length INTEGER CHECK(length > 0),
                        PRIMARY KEY(band, song, length));

CREATE TABLE Listeners(name VARCHAR(20) NOT NULL PRIMARY KEY,
                       country VARCHAR(20));

CREATE TABLE Fans(listener VARCHAR(20) NOT NULL REFERENCES Listeners(name),
                  band VARCHAR(20) NOT NULL REFERENCES Bands(name),
                  rating SMALLINT CHECK(rating > 0),
                  PRIMARY KEY(listener, band));

CREATE TABLE Likes(listener VARCHAR(20) NOT NULL REFERENCES Listeners(name),
                   song VARCHAR(30) NOT NULL REFERENCES Songs(name),
                   PRIMARY KEY(listener, song));

INSERT INTO Bands VALUES('Aerosmith', 'USA', 1970);
INSERT INTO Bands VALUES('The Beatles', 'England', 1960);
INSERT INTO Bands VALUES('The Cranberries', 'Ireland', 1989);
INSERT INTO Bands VALUES('David Bowie', 'England', 1967);
INSERT INTO Bands VALUES('Earth, Wind & Fire', 'USA', 1969);
INSERT INTO Bands VALUES('Foo Fighters', 'USA', 1994);
INSERT INTO Bands VALUES('The Grateful Dead', 'USA', 1965);

INSERT INTO Songs VALUES('Across the Universe', 'John Lennon');
INSERT INTO Songs VALUES('Blackbird', 'Paul McCartney');
INSERT INTO Songs VALUES('Come Together', 'John Lennon, Paul McCartney');
INSERT INTO Songs VALUES('Dreams', 'Dolores O''Riordan, Noel Hogan');
INSERT INTO Songs VALUES('Everlong', 'Dave Grohl');
INSERT INTO Songs VALUES('Fantasy', 'Maurice White, Verdine White, Eddie del Barrio');
INSERT INTO Songs VALUES('Got to Get You Into My Life', 'John Lennon, Paul McCartney');
INSERT INTO Songs VALUES('Heroes', 'David Bowie, Brian Eno');
INSERT INTO Songs VALUES('In My Life', 'John Lennon, Paul McCartney');
INSERT INTO Songs VALUES('Janie''s Got a Gun', 'Steven Tyler, Tom Hamilton');
INSERT INTO Songs VALUES('Kings and Queens', 'Steven Tyler, Brad Whitford, Tom Hamilton, Joey Kramer, Jack Douglas');
INSERT INTO Songs VALUES('Linger', 'Dolores O''Riordan, Noel Hogan');

INSERT INTO Recordings VALUES('Aerosmith', 'Come Together', 224);
INSERT INTO Recordings VALUES('Aerosmith', 'Janie''s Got a Gun', 328);
INSERT INTO Recordings VALUES('Aerosmith', 'Kings and Queens', 295);
INSERT INTO Recordings VALUES('The Beatles', 'Across the Universe', 228);
INSERT INTO Recordings VALUES('The Beatles', 'Blackbird', 138);
INSERT INTO Recordings VALUES('The Beatles', 'Come Together', 259);
INSERT INTO Recordings VALUES('The Beatles', 'Kansas City', 148);
INSERT INTO Recordings VALUES('The Beatles', 'Got to Get You Into My Life', 148);
INSERT INTO Recordings VALUES('The Beatles', 'In My Life', 148);
INSERT INTO Recordings VALUES('The Cranberries', 'Dreams', 272);
INSERT INTO Recordings VALUES('The Cranberries', 'Linger', 274);
INSERT INTO Recordings VALUES('David Bowie', 'Across the Universe', 273);
INSERT INTO Recordings VALUES('David Bowie', 'Heroes', 367);
INSERT INTO Recordings VALUES('David Bowie', 'Heroes', 212);
INSERT INTO Recordings VALUES('Earth, Wind & Fire', 'Fantasy', 278);
INSERT INTO Recordings VALUES('Earth, Wind & Fire', 'Got to Get You Into My Life', 278);
INSERT INTO Recordings VALUES('Foo Fighters', 'Everlong', 250);

INSERT INTO Listeners VALUES('Amy', 'USA');
INSERT INTO Listeners VALUES('Ben', 'England');
INSERT INTO Listeners VALUES('Carlos', 'Brazil');
INSERT INTO Listeners VALUES('Dan', 'England');
INSERT INTO Listeners VALUES('Eve', 'Australia');

INSERT INTO Fans VALUES('Amy', 'The Cranberries', 5);
INSERT INTO Fans VALUES('Ben', 'The Cranberries', 3);
INSERT INTO Fans VALUES('Ben', 'David Bowie', 4);
INSERT INTO Fans VALUES('Ben', 'Earth, Wind & Fire', 2);
INSERT INTO Fans VALUES('Carlos', 'Aerosmith', 3);
INSERT INTO Fans VALUES('Carlos', 'The Beatles', 3);
INSERT INTO Fans VALUES('Carlos', 'Foo Fighters', 5);
INSERT INTO Fans VALUES('Dan', 'Aerosmith', 5);
INSERT INTO Fans VALUES('Dan', 'The Beatles', 2);
INSERT INTO Fans VALUES('Dan', 'The Cranberries', 2);
INSERT INTO Fans VALUES('Dan', 'David Bowie', 5);
INSERT INTO Fans VALUES('Dan', 'Earth, Wind & Fire', 4);
INSERT INTO Fans VALUES('Eve', 'The Cranberries', 4);

INSERT INTO Likes VALUES('Amy', 'Across the Universe');
INSERT INTO Likes VALUES('Amy', 'Come Together');
INSERT INTO Likes VALUES('Ben', 'Across the Universe');
INSERT INTO Likes VALUES('Ben', 'Blackbird');
INSERT INTO Likes VALUES('Ben', 'Got to Get You Into My Life');
INSERT INTO Likes VALUES('Carlos', 'Dreams');
INSERT INTO Likes VALUES('Dan', 'Across the Universe');
INSERT INTO Likes VALUES('Dan', 'Blackbird');
INSERT INTO Likes VALUES('Dan', 'Come Together');
INSERT INTO Likes VALUES('Dan', 'Dreams');
INSERT INTO Likes VALUES('Dan', 'Everlong');
INSERT INTO Likes VALUES('Dan', 'Heroes');
INSERT INTO Likes VALUES('Dan', 'Linger');
INSERT INTO Likes VALUES('Eve', 'Across the Universe');
INSERT INTO Likes VALUES('Eve', 'Come Together');

