CREATE TABLE movies_movie_tag
(movie_id int references movies(id),
 movie_tag_id int references movie_tag(id),
 unique(movie_id, movie_tag_id));
