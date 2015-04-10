-- the table movies didn't previously have a primary key, simply the id was a
-- SERIAL
ALTER TABLE movies ADD PRIMARY KEY (id);

-- now that the id column on the movies table is unique, we can create a table
-- that references it as a foreign key
CREATE TABLE movie_position
(movie_id int references movies(id) PRIMARY KEY,
 position_prev int references movies(id),
 position_next int references movies(id));
