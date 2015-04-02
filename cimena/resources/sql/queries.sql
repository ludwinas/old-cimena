--name: create-movie!
-- creates a new movie record
INSERT INTO movies
(title, link, is_watched)
VALUES (:title, :link, :is_watched)

--name: update-movie!
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

-- name: delete-movie!
-- deletes the movie with the given id
DELETE FROM movies
WHERE id = :id
