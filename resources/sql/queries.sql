--name: create-movie!
-- creates a new user record
INSERT INTO movies
(id, name, imdb_link, is_watched, in_progress, description)
VALUES (:id, :imdb_link, :is_watched, :in_progress, :description)

--name: update-movie!
-- update an existing user record
UPDATE movies
SET name = :name, imdb_link = :imdb_link, is_watched = :is_watched, in_progress = :in_progress, description = :description
WHERE id = :id

-- name: get-movie
-- retrieve a used given the id.
SELECT * FROM movies
WHERE id = :id

--name: get-movies
-- selects all available messages
SELECT * from movies
