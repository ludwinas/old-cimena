ALTER TABLE movie_position
DROP CONSTRAINT movie_position_movie_id_fkey,
ADD CONSTRAINT movie_position_movie_id_fkey
   FOREIGN KEY (movie_id)
   REFERENCES movies(id);
