(ns cimena.routes.home
  (:require [cimena.layout :as layout]
            [cimena.db.core :as db]
            [cimena.db.helpers :as dbh]
            [cimena.movie.core :as m]
            [cimena.lib.util :as util]
            [compojure.core :refer [defroutes GET POST]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [redirect response]]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

(defn update-movie! [params]
  ;; when the movie's new state contains is_watched = true
  (when (:is_watched params)
    ;; check whether we're marking an ordered movie as watched
    (let [movie-id (:id params)
          ordered? (dbh/is-movie-in-ordered-list? movie-id)]
      (when ordered?
        ;; In this case we're marking an ordered movie as watched, this means
        ;; that we should remove it from the ordered list, as it's pointless to
        ;; have watched movies in there
        (m/archive-movie! movie-id))))
  (db/update-movie! params))

(defn validate-movie [params]
  (first
   (b/validate
    params
    :title v/required
    :link v/required)))


(defn save-movie! [{:keys [params]}]
  (if-let [errors (validate-movie params)]
    (-> (redirect (str "/movie/" (:id params))) ;; redirect back to the same page
        (assoc :flash (assoc params :errors (vals errors))))
    ;; at this point we know that the content is valid
    ;; and we want to figure out whether it's a create or update
    ;; TODO: outsource this to a db helper (at least building the query params,
    ;; seing as there's a bunch of logic embedded in the update function
    (do
      (let [movie-id (:id params)
            query-params (assoc (select-keys params [:title :link])
                                :is_watched (util/not-nil? (:is_watched params))
                                :id (util/int-or-nil movie-id))
            movie-tags (util/into-a-vec (:movie-tags params))]
        (if (m/is-a-movie? movie-id)
          ;; we use the function because some archiving logic is necessary before
          ;; we perform the actual db query
          (do
            (update-movie! query-params)
            (dbh/movie-add-tags! movie-id movie-tags))
          (let [new-movie (db/create-movie<! query-params)
                new-movie-id (:id new-movie)]
            (dbh/movie-add-tags! new-movie-id movie-tags))))
      (redirect "/"))))

(defn delete-movie! [{:keys [params]}]
  (db/delete-movie! {:id (util/int-or-nil (:id params))})
  (response {:status "OK"}))

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (let [movies (dbh/get-movies)
         movie-positions (db/get-movie-positions)
         movies-sorted (m/sort-movies movies movie-positions)
         ;; movies-unsorted is the list of movies in the "inbox" area
         ;; the ones that I have just created and I haven't ordered yet
         ;; they are thus the ones that exist, but are missing from the ordered list
         ;; minus, of course, the watched ones
         movies-unsorted (->> (util/set-difference movies movies-sorted)
                              (filter (complement :is_watched)))]
     {:movies movies-sorted :movies-unsorted movies-unsorted})))

(defn edit-movie [{:keys [params flash]}]
  (let [movie-id (:id params)
        movie (dbh/movie-or-nil movie-id)
        all-movie-tags (db/get-movie-tags)
        this-movie-tags (set (dbh/movie-get-tags movie-id))
        ;; adds a new selected : boolean to the movie tags list based on whether
        ;; the movie tag is already used for this movie or not
        ;; this is because we cannot check list membership in the template
        movie-tags (map
                    #(merge {:selected (contains? this-movie-tags (:id %))} %)
                    all-movie-tags)]
     (layout/render "movie-edit.html" (merge
                                       {:movie movie}
                                       {:movie-tags movie-tags}
                                       (select-keys flash [:errors])))))

(defn update-positions! [{:keys [params]}]
  (let [movie-ids (:positions params)]
    (m/store-positions-from-list! movie-ids))
  (response {:status "OK"}))

(defn list-watched [{:keys [flash]}]
  (layout/render "watched.html"
                 {:movies (db/get-watched-movies)}))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/movie/:id" request (edit-movie request))
  (POST "/movie/:id" request (save-movie! request))
  (POST "/movie/:id/delete" request (delete-movie! request))
  (POST "/update-positions" request (update-positions! request))
  (GET "/list-watched" request (list-watched request)))
