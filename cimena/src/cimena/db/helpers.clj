(ns cimena.db.helpers
  (:require [cimena.db.core :as db]
            [cimena.lib.util :as util]))

(defn movie-or-nil [id]
  (first (db/get-movie {:id (util/int-or-nil id)})))

(defn delete-movie-position! [movie-id]
  (let [query-params {:movie_id movie-id}]
    (db/delete-movie-position! query-params)))

(defn is-movie-in-ordered-list? [movie-id]
  (let [query-params {:movie_id movie-id}]
    (-> (db/is-movie-in-ordered-list? query-params)
        (first)
        :count
        pos?)))
