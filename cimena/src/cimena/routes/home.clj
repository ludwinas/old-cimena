(ns cimena.routes.home
  (:require [cimena.layout :as layout]
            [cimena.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [redirect response]]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

(defn not-nil? [c]
  (not
   (nil? c)))

(defn int-or-nil [x]
  (try (Integer/parseInt x)
       (catch NumberFormatException e nil)))

(defn movie-or-nil [id]
  (first (db/get-movie {:id (int-or-nil id)})))

(defn is-a-movie? [id]
  (not-nil? (movie-or-nil id)))

(defn format-prev-next [item prev-item next-item]
  {:movie_id (int-or-nil item) :position_prev (int-or-nil prev-item) :position_next (int-or-nil next-item)})

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

(defn get-item-with-keyword [keyword id coll]
  (some #(when (= id (keyword %)) %)
        coll))

(defn get-item-from-movies [movies movie-id]
  (get-item-with-keyword :id movie-id movies))

(defn get-item-from-movie-positions [movie-positions movie-position-id]
  (get-item-with-keyword :movie_id movie-position-id movie-positions))

(defn get-next [position movie-positions]
  (let [next-id (:position_next position)]
    (get-item-from-movie-positions movie-positions next-id)))

(defn walk-positions [current movie-positions]
  (when current ;; to avoid trying to get the next one when current is nil
    (cons (:movie_id current)
        (walk-positions (get-next current movie-positions) movie-positions))))

(defn sort-movies [movies movie-positions]
  (let [first-position (some #(when (-> % :position_prev (= nil)) %) movie-positions)
        order (walk-positions first-position movie-positions)]
    ;; now that I have an ordered list of the movie-ids I can just create an
    ;; ordered list
    (map (fn movie-from-index [current-id]
            (get-item-from-movies movies current-id))
          order)))

(defn validate-message [params]
  (first
   (b/validate
    params
    :title v/required
    :link v/required)))

(defn save-movie! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (redirect (str "/movie/" (:id params))) ;; redirect back to the same page
        (assoc :flash (assoc params :errors (vals errors))))
    ;; at this point we know that the content is valid
    ;; and we want to figure out whether it's a create or update
    (do
      (let [query-params
            (assoc (select-keys params [:title :link])
                   :is_watched (not-nil? (:is_watched params))
                   :id (int-or-nil (:id params)))]
        (if (is-a-movie? (:id params))
          (db/update-movie! query-params)
          (db/create-movie! query-params)))
      (redirect "/"))))

(defn delete-movie! [{:keys [params]}]
  (db/delete-movie! {:id (int-or-nil (:id params))})
  (response {:status "OK"}))

(defn set-difference [coll1 coll2]
  (let [first-set (into #{} coll1)
        second-set (into #{} coll2)]
        (clojure.set/difference first-set second-set)))

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   ;; here I have to sort
   (let [movies (db/get-movies)
         movie-positions (db/get-movie-positions)
         movies-sorted (sort-movies movies movie-positions)
         movies-unsorted (set-difference movies movies-sorted)]
     {:movies movies-sorted :movies-unsorted movies-unsorted})))

(defn edit-movie [{:keys [params flash]}]
  (let [movie-id (int-or-nil (:id params))]
    (let [movie (-> {:id [movie-id]}
                    (db/get-movie)
                    (first))]
      (layout/render "movie-edit.html" (merge
                                        {:movie movie}
                                        (select-keys flash [:errors]))))))

(defn about-page []
  (layout/render "about.html"))

(defn update-positions! [{:keys [params]}]
  ;; Here I'll make a linked list of the parameters I got from the frontend
  (let [movies (:positions params)
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
        updates (clojure.set/difference (into #{} (get updates-inserts false)) old-movie-positions)]
    ;; We'll first deal with the inserts
    (doall (map db/create-movie-position! inserts))
    ;; then updates
    (doall (map db/update-movie-position! updates))
    )
  (response {:status "OK"}))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/movie/:id" request (edit-movie request))
  (POST "/movie/:id" request (save-movie! request))
  (POST "/movie/:id/delete" request (delete-movie! request))
  (POST "/update-positions" request (update-positions! request))
  (GET "/about" [] (about-page)))
