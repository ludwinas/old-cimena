(ns cimena.db.helpers
  (:require [cimena.db.core :as db]
            [cimena.lib.util :as util]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

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

(defn movie-tag-or-nil [id]
  (first (db/get-movie-tag {:id (util/int-or-nil id)})))

(defn build-movie-tag-query-params [params]
  (assoc (select-keys params [:label :color])
         :id (util/int-or-nil (:id params))))

(defn store-movie-tag! [params]
  (let [query-params (build-movie-tag-query-params params)]
  (if (util/not-nil? (movie-tag-or-nil (:id params)))
    (db/update-movie-tag! query-params)
    (db/create-movie-tag! query-params))))

(defn delete-movie-tag! [id]
  (db/delete-movie-tag! {:id (util/int-or-nil id)}))
