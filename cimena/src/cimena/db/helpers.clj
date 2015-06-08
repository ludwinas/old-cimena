(ns cimena.db.helpers
  (:require [cimena.db.core :as db]
            [cimena.lib.util :as util]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

(defn movie-or-nil [id]
  (first (db/get-movie {:id (util/int-or-nil id)})))

(defn is-a-movie? [id]
  (util/not-nil? (movie-or-nil id)))

(defn movie-tag-or-nil [id]
  (first (db/get-movie-tag {:id (util/int-or-nil id)})))

(defn is-a-movie-tag? [id]
  (util/not-nil? (movie-tag-or-nil id)))

(defn movie-get-tags-from-list [id tags-list]
  (map :movie_tag_id (filter #(= (:movie_id %) id) tags-list)))

(defn get-tag-by-id [tag-id movie-tags]
  (util/get-item-with-keyword :id tag-id movie-tags))

(defn merge-tag-info
  "Expects a collection of movies as it would be retrieved from the database;
  merges tag information into the movie record hashmap."
  [list-of-movies]
  (let [movie-tags (db/get-movie-tags)
        movies-movie-tags (db/get-movies-movie-tags)]
        ;; For each movie we first look up which movie tags are associated with
        ;; it and then, by id, we retrieve tag information. Then we merge that
        ;; tag information with the original movie hashmap
    (map (fn [current-movie]
           (merge current-movie
                  {:tags (map
                          #(get-tag-by-id % movie-tags)
                          (movie-get-tags-from-list (:id current-movie) movies-movie-tags))}))
         list-of-movies)))

(defn get-movies
  []
  (let [movies (db/get-movies)]
    (merge-tag-info movies)))

(defn get-watched-movies
  []
  (let [watched-movies (db/get-watched-movies)]
    (merge-tag-info watched-movies)))

(defn get-movies-by-tag
  [id]
  (let [tag-id (util/int-or-nil id)
        movies-by-tag (db/get-movies-by-tag {:movie_tag_id tag-id})]
    (info movies-by-tag)
    (merge-tag-info movies-by-tag)))

(defn build-movie-tag-query-params [params]
  (assoc (select-keys params [:label :color])
         :id (util/int-or-nil (:id params))))

(defn store-movie-tag! [params]
  (let [query-params (build-movie-tag-query-params params)]
  (if (is-a-movie-tag? (:id params))
    (db/update-movie-tag! query-params)
    (db/create-movie-tag! query-params))))

(defn delete-movie-tag! [id]
  (db/delete-movie-tag! {:id (util/int-or-nil id)}))

(defn movie-add-tags! [id movie-tag-ids]
  "expects a movie id and a vector containing the list of movie tags that are to
  be associated with it"
  (let [movie-id (util/int-or-nil id)
        existing-ids (->> (db/movie-get-tags {:movie_id movie-id})
                          (map :movie_tag_id))
        ;; data-diff returns a tuple of [things-only-in-a things-only-in-b
        ;; things-in-both]
        difference (util/data-diff existing-ids movie-tag-ids)
        deletes (get difference 0) ;; things only in existing-ids
        inserts (get difference 1)] ;; things only in movie-tag-ids
    ;; delete existing tags that are missing from the list of ids
    (doseq [tag-id deletes]
      (db/movie-delete-tag! {:movie_id movie-id
                             :movie_tag_id (util/int-or-nil tag-id)}))
    ;; and then add new ones!
    (doseq [tag-id inserts]
      (db/movie-add-tag! {:movie_id movie-id
                          :movie_tag_id (util/int-or-nil tag-id)}))
    ))

(defn movie-get-tags [movie-id]
  (let [query-params {:movie_id (util/int-or-nil movie-id)}]
    (->> (db/movie-get-tags query-params)
         (map :movie_tag_id))))

(defn save-movie! [movie-id movie-tags query-params]
  (if (is-a-movie? movie-id)
    ;; update
    (do
      (movie-add-tags! movie-id movie-tags)
      (db/update-movie! query-params))
    ;; insert
    (let [new-movie (db/create-movie<! query-params)
          new-movie-id (:id new-movie)]
      (movie-add-tags! new-movie-id movie-tags))))
