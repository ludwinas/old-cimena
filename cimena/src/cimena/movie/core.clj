(ns cimena.movie.core
  (:require [cimena.db.core :as db]
            [cimena.db.helpers :as dbh]
            [cimena.lib.util :as util]))

(defn find-obsolete [old-movie-positions movies]
  (let [old-ids (map :movie_id old-movie-positions)
        new-ids (map :movie_id movies)]
    (util/set-difference old-ids new-ids)))

(defn unique-ids [coll]
  "creates a set of movie_id values from a collection of maps, we'll use this to
  compare the sets of ids and determine the difference"
  (->>  (map :movie_id coll)
        (into #{})))

(defn split-inserts [old new]
  "splits an old and a new collection between existing id values and new id
  values"
  (let [ids (clojure.set/difference (unique-ids new) (unique-ids old))]
    ;; in this case contains? can be used to check set membership
    (group-by (fn [x] (contains? ids (:movie_id x))) new)))

(defn format-prev-next [item prev-item next-item]
  {:movie_id (util/int-or-nil item)
   :position_prev (util/int-or-nil prev-item)
   :position_next (util/int-or-nil next-item)})

(defn get-item-from-movie-positions [movie-positions movie-position-id]
  (util/get-item-with-keyword :movie_id movie-position-id movie-positions))

(defn get-next [position movie-positions]
  (let [next-id (:position_next position)]
    (get-item-from-movie-positions movie-positions next-id)))

(defn walk-positions [current movie-positions]
  (when current ;; to avoid trying to get the next one when current is nil
    (cons (:movie_id current)
          (walk-positions (get-next current movie-positions) movie-positions))))

(defn get-order-from-movie-positions [movie-positions]
  (let [first-position (some #(when (-> % :position_prev (= nil)) %) movie-positions)]
    (walk-positions first-position movie-positions)))

(defn is-a-movie? [id]
  (util/not-nil? (dbh/movie-or-nil id)))

(defn get-item-from-movies [movies movie-id]
  (util/get-item-with-keyword :id movie-id movies))



(defn sort-movies [movies movie-positions]
  (let [order (get-order-from-movie-positions movie-positions)]
    ;; now that I have an ordered list of the movie-ids I can just create an
    ;; ordered list
    (map (fn movie-from-index [current-id]
            (get-item-from-movies movies current-id))
          order)))

(defn store-positions-from-list! [movie-ids]
  ;; Here I'll make a linked list of the parameters I got from the frontend
  (let [movies (vec movie-ids)
        prev-movies (cons nil movies)
        next-movies (drop 1 (conj movies nil))
        movie-positions (into #{} (map format-prev-next movies prev-movies next-movies))
        old-movie-positions (into #{} (db/get-movie-positions))
        ;; split-inserts groups the list between new id values (that are
        ;; missing from the old-movie-positions list) and all the other ones.
        ;; The maps that contains ids missing from old-movie-positions are sure
        ;; to be inserts.
        updates-inserts (split-inserts old-movie-positions movie-positions)
        inserts (get updates-inserts true) ;; true = insert
        ;; To determine instead which are updates and which can be omitted, we
        ;; must compare the update candidates with the existing list
        updates (util/set-difference (get updates-inserts false) old-movie-positions)
        obsolete (find-obsolete old-movie-positions movie-positions)]
    ;; We'll first deal with the inserts
    (doall (map db/create-movie-position! inserts))
    ;; then updates
    (doall (map db/update-movie-position! updates))
    ;; then we'll clean up those records in the movie-position table
    ;; that are obsolete
    (doall (map dbh/delete-movie-position! obsolete))))

(defn archive-movie! [id]
  "archiving a movie is the process of removing it from the ordered
  list. specifically we retrieve the ordered list, remove the provided id, then
  reorder the list"
  (let [movie-positions (db/get-movie-positions)
        order (get-order-from-movie-positions movie-positions)
        new-order (filter #(not= % id) order)]
    (store-positions-from-list! new-order)))
