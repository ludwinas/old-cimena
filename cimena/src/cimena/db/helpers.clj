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

(defn movie-add-tags! [id movie-tag-ids]
  "expects a movie id and a vector containing the list of movie tags that are to
  be associated with it"
  (let [movie-id (util/int-or-nil id)
        existing-tags (db/movie-get-tags {:movie_id movie-id})
        existing-ids (map :movie_tag_id existing-tags)]
    ;; delete existing tags that are missing from the list of ids
    ;; and then add new ones!
    ;; (doseq [tag-id movie-tag-ids]
    ;;   (db/movie-add-tag! {:movie_id movie-id
    ;;                       :movie_tag_id (util/int-or-nil tag-id)}))
  ))

(defn movie-get-tags [movie-id]
  (let [query-params {:movie_id (util/int-or-nil movie-id)}]
    (->> (db/movie-get-tags query-params)
         (map :movie_tag_id))))
