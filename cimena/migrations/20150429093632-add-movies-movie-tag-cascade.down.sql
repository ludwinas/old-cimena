-- the constraint to movie_tag(id)
ALTER TABLE movies_movie_tag
DROP CONSTRAINT movies_movie_tag_movie_tag_id_fkey,
ADD CONSTRAINT movies_movie_tag_movie_tag_id_fkey
    FOREIGN KEY (movie_tag_id)
    REFERENCES movie_tag(id);
-- the constraint to movie(id)
ALTER TABLE movies_movie_tag
DROP CONSTRAINT movies_movie_tag_movie_id_fkey,
ADD CONSTRAINT movies_movie_tag_movie_id_fkey
    FOREIGN KEY (movie_id)
    REFERENCES movies(id);
