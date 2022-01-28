-- Questions
-- a. For each song that Dan likes, find the names of bands that recorded the longest version.
-- Format your output as list of (song, band) pairs (x) [Output columns: song, band]
-- b. Find listener pairs (listener1, listener2) such that the songs liked by listener1 is a subset of
-- songs liked by listener2. Format your output as list of (listener1, listener2) pairs (x)
-- [Output columns: listener1, listener2]
-- c. Find names of all listeners who are fans of only bands that recorded only songs they like. (x)
-- [Output columns: name]
-- d. For each song, find the number of listeners who like it, as well as its average length as
-- recorded by bands. Sort the output by the number of listeners who like the song (the most
-- popular song should be listed first). In the case of ties, sort by song name (ascending). If
-- a song is not recorded by any band, its average length should be listed as NULL.(x) [Output
-- columns: song, cnt, avg]
-- e. Find, for each listener, the band(s) he or she rates the highest. The output should be list
-- of (listener, band) pairs. If some listener L does not like any band, you should still output
-- (L, NULL).(x)[Output column: name, band]



-- (a) Find names of all bands that Ben is a fan of.
SELECT band
FROM Fans
WHERE listener = 'Ben';

-- (b) Find names and countries of listeners who rate The Cranberries higher than 3.
SELECT listener, country
FROM Fans
JOIN Listeners ON Fans.listener = Listeners.name
WHERE Fans.rating > 3 AND fans.band = 'The Cranberries';

-- (c) Find names of bands that recorded some song Eve likes with length no more than 4 minutes (240 seconds).
SELECT DISTINCT band, length
FROM Recordings
JOIN Likes ON Likes.song = Recordings.song AND Likes.listener = 'Eve'
WHERE Recordings.length <= 240;

-- (d) Find names of all listeners who like Across the Universe but not Come Together.
SELECT listener
FROM Likes l1
WHERE
	song = 'Across the Universe' AND
	NOT EXISTS (
		SELECT *
		FROM Likes l2
		WHERE
			l1.listener = l2.listener AND
            l2.song = 'Come Together');

SELECT l1.listener
FROM Likes l1
LEFT JOIN (
	SELECT *
	FROM Likes l2
	WHERE l2.song = 'Come Together') l2
ON l1.listener = l2.listener
WHERE l1.song = 'Across the Universe' AND l2.song IS NULL;

-- (e) For each song that Dan likes, find the names of bands that recorded it at the shortest length.
SELECT
	song,
	(SELECT band
	FROM Recordings
	WHERE Likes.song = Recordings.song
	ORDER BY length
	LIMIT 1) as band
FROM Likes
WHERE listener = 'Dan';

-- (f) Find the shortest and second shortest songs and who recorded them; i.e., return the
--     Recordings rows with the lowest and second shoprtest length. (This query specification is ambiguous in the
--     case of ties; feel free to use your interpretation but state it clearly in comments)
SELECT song, band
FROM Recordings
ORDER BY length
LIMIT 2;

-- Two shortest distinct songs
SELECT *
FROM Recordings s1
WHERE length <= ALL(
	SELECT length
	FROM Recordings s2
	WHERE song <> (
		SELECT song
		FROM Recordings s3
		WHERE length <= ALL(
			SELECT length
			FROM Recordings)));

-- Two shortest distinct songs
SELECT *
FROM Recordings s1
WHERE length <= ALL(
	SELECT length -- length of all other songs
	FROM Recordings s2
	WHERE song <> (
		SELECT song -- shortest song
		FROM Recordings s3
		WHERE length <= ALL(
			SELECT length
			FROM Recordings)))
	AND
	s1.song <> (
		SELECT song -- shortest song
		FROM Recordings s3
		WHERE length <= ALL(
			SELECT length
			FROM Recordings))
UNION
SELECT * -- shortest song
FROM Recordings s3
WHERE length <= ALL(
	SELECT length
	FROM Recordings);

SELECT s1.song, band
FROM Recordings s1
JOIN (
	SELECT song, min(length) as length
	FROM Recordings s2
	GROUP BY song
	ORDER BY length
	LIMIT 2) best2
ON s1.length = best2.length AND s1.song = best2.song;

-- (g) For each listener, find names of bars frequented by the listener that serve none of the beers liked by
--     the listener.

-- (h) Find names of all listeners who frequent only those bars that serve some beers they like.
-- (i) Find names of all listeners who frequent every band that recordings some beers they like.

-- Find those songs that are the unique song by their writer.
SELECT name
FROM Songs b1
WHERE NOT EXISTS (
	SELECT *
	FROM Songs
	WHERE writer = b1.writer AND name <> b1.name);

