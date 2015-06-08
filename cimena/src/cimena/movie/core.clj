(ns cimena.movie.core
  (:require [cimena.db.core :as db]
            [cimena.db.helpers :as dbh]
            [cimena.lib.util :as util]))

(defn is-a-movie? [id]
  (util/not-nil? (dbh/movie-or-nil id)))

(defn get-sidebar-info [active-page]
  (let [movies (dbh/get-movies)
        movies-to-watch (filter (complement :is_watched) movies)]
    {:movies-to-watch (count movies-to-watch)
     :movies-watched (- (count movies) (count movies-to-watch))
     :tags []
     :active active-page}))
