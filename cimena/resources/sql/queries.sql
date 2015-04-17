-- name: create-movie!
-- creates a new movie record
INSERT INTO movies
(title, link, is_watched)
VALUES (:title, :link, :is_watched)

-- name: update-movie!
-- update an existing movie record
UPDATE movies
SET title = :title, link = :link, is_watched = :is_watched
WHERE id = :id

-- name: get-movie
-- retrieve a used given the id.
SELECT * FROM movies
WHERE id = :id

-- name: get-movies
-- gets all the movies
SELECT * FROM movies

-- name: get-watched-movies
-- gets all the movies
SELECT * FROM movies WHERE is_watched = true ORDER BY title

-- name: delete-movie!
-- deletes the movie with the given id
DELETE FROM movies
WHERE id = :id

-- name: create-movie-position!
INSERT INTO movie_position
(movie_id, position_prev, position_next)
VALUES (:movie_id, :position_prev, :position_next)

-- name: update-movie-position!
UPDATE movie_position
SET position_prev = :position_prev, position_next = :position_next
WHERE movie_id = :movie_id

-- name: get-movie-positions
SELECT * from movie_position

-- name: is-movie-in-ordered-list?
SELECT COUNT(1) from movie_position where movie_id = :movie_id

-- name: delete-movie-position!
DELETE FROM movie_position
WHERE movie_id = :movie_id
