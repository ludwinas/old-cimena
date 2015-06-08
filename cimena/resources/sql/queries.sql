-- name: create-movie<!
-- creates a new movie record
INSERT INTO movies
(title, link, is_watched, original_title)
VALUES (:title, :link, :is_watched, :original_title);

-- name: update-movie!
-- update an existing movie record
UPDATE movies
SET title = :title, link = :link, is_watched = :is_watched, original_title = :original_title
WHERE id = :id;

-- name: get-movie
-- retrieve a used given the id.
SELECT * FROM movies
WHERE id = :id;

-- name: get-movies
-- gets all the movies
SELECT * FROM movies;

-- name: get-watched-movies
-- gets all the movies
SELECT * FROM movies WHERE is_watched = true ORDER BY title;

-- name: delete-movie!
-- deletes the movie with the given id
DELETE FROM movies
WHERE id = :id;

-- name: create-movie-tag!
-- creates a new movie record
INSERT INTO movie_tag
(label, color)
VALUES (:label, :color);

-- name: update-movie-tag!
-- update an existing movie record
UPDATE movie_tag
SET label = :label, color = :color
WHERE id = :id;

-- name: get-movie-tag
-- retrieve a movie-tag given the id.
SELECT * FROM movie_tag
WHERE id = :id;

-- name: get-movie-tags
-- gets all the movie-tags
SELECT * FROM movie_tag;

-- name: delete-movie-tag!
-- deletes the movie-tag with the given id
DELETE FROM movie_tag
WHERE id = :id;

-- name: movie-add-tag!
-- associates a movie with a movie_tag
INSERT INTO movies_movie_tag
(movie_id, movie_tag_id)
VALUES (:movie_id, :movie_tag_id);

-- name: movie-delete-tag!
DELETE FROM movies_movie_tag
WHERE movie_id = :movie_id AND movie_tag_id = :movie_tag_id;

-- name: movie-get-tags
-- gets all the movie-tags that correspond to the provided movie
SELECT movie_tag_id FROM movies_movie_tag
WHERE movie_id = :movie_id;

-- name: get-movies-movie-tags
SELECT * FROM movies_movie_tag;

-- name: get-movie-tags-with-count
-- gets all the movie-tags with an extra column, count, that represents how many
-- movies have been assigned that tag
SELECT id, label, color, (SELECT COUNT(1)
                          FROM movies_movie_tag
                          WHERE movies_movie_tag.movie_tag_id = id)
                                FROM movie_tag;

-- name: get-movies-by-tag
-- gets all the movies that correspond to a specific tag
SELECT m.id, m.title, m.link, m.is_watched, m.original_title FROM movies m
LEFT JOIN movies_movie_tag mmt
     ON mmt.movie_id = m.id
WHERE movie_tag_id = :movie_tag_id;
