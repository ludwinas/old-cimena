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
    ;; do here is necessary because we want to both store the movie and redirect
    (do
      (let [movie-id (:id params)
            query-params (assoc (select-keys params [:title :link :original_title])
                                :is_watched (util/not-nil? (:is_watched params))
                                :id (util/int-or-nil movie-id))
            movie-tags (util/into-a-vec (:movie-tags params))]
        (dbh/save-movie! movie-id movie-tags query-params))
      (redirect "/"))))

(defn delete-movie! [{:keys [params]}]
  (db/delete-movie! {:id (util/int-or-nil (:id params))})
  (response {:status "OK"}))

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (let [movies (dbh/get-movies)
         ;; filter out the movies that have been marked as watched
         movies-to-watch (filter (complement :is_watched) movies)]
     {:movies movies-to-watch
      :sidebar-info (m/get-sidebar-info "home")})))

(defn edit-movie [{:keys [params flash]}]
  (let [movie-id (:id params)
        movie-record (dbh/movie-or-nil movie-id)
        movie (if (not (nil? movie-record))
                movie-record
                {:id "new"})
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

;; this function is called when we want to populate a new movie entry with info
;; gathered from the TMDB api
(defn populate-new-movie [{:keys [params]}]
  (let [movie {:title (:title params)
               :original_title (:original_title params)
               :link (:link params)
               :id "new"}
        tags (db/get-movie-tags)]
    (layout/render "movie-edit.html"
                   {:movie movie
                    :movie-tags tags})))

(defn list-watched [{:keys [flash]}]
  (layout/render "watched.html"
                 {:movies (dbh/get-watched-movies)
                  :sidebar-info (m/get-sidebar-info "watched")}))

(defn get-movies-by-tag
  [request]
  (layout/render
   "home.html"
   (let [tag-id (get-in request [:params :tag-id])
         movies (dbh/get-movies-by-tag tag-id)]
     {:movies movies
      :sidebar-info (m/get-sidebar-info "home")})))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/movie/:id" request (edit-movie request))
  (POST "/movie/:id" request (save-movie! request))
  (POST "/movie/:id/delete" request (delete-movie! request))
  (GET "/movie/new/populate" request (populate-new-movie request))
  (GET "/movies-by-tag/:tag-id" [] get-movies-by-tag)
  (GET "/watched" request (list-watched request)))
