-- in order to add "on delete cascade" for these constraints
-- I have to drop them and create them again
ALTER TABLE movie_position
DROP CONSTRAINT movie_position_movie_id_fkey,
ADD CONSTRAINT movie_position_movie_id_fkey
   FOREIGN KEY (movie_id)
   REFERENCES movies(id)
   ON DELETE CASCADE;
