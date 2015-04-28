-- name: create-movie<!
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

-- name: create-movie-tag!
-- creates a new movie record
INSERT INTO movie_tag
(label, color)
VALUES (:label, :color)

-- name: update-movie-tag!
-- update an existing movie record
UPDATE movie_tag
SET label = :label, color = :color
WHERE id = :id

-- name: get-movie-tag
-- retrieve a movie-tag given the id.
SELECT * FROM movie_tag
WHERE id = :id

-- name: get-movie-tags
-- gets all the movie-tags
SELECT * FROM movie_tag

-- name: delete-movie-tag!
-- deletes the movie-tag with the given id
DELETE FROM movie_tag
WHERE id = :id

-- name: movie-add-tag!
-- associates a movie with a movie_tag
INSERT INTO movies_movie_tag
(movie_id, movie_tag_id)
VALUES (:movie_id, :movie_tag_id)

-- name: movie-delete-tag!
DELETE FROM movies_movie_tag
WHERE movie_id = :movie_id AND movie_tag_id = :movie_tag_id

-- name: movie-get-tags
-- gets all the movie-tags that correspond to the provided movie
SELECT movie_tag_id FROM movies_movie_tag
WHERE movie_id = :movie_id

-- name: get-movies-movie-tags
SELECT * FROM movies_movie_tag
