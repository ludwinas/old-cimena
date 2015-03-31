(ns cimena.routes.home
  (:require [cimena.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [cimena.db.core :as db]
            [bouncer.core :as b]
            [ring.util.response :refer [redirect]]
            [bouncer.validators :as v]))

;; name = :name, imdb_link = :imdb_link, is_watched = :is_watched,
;; in_progress = :in_progress, description = :description

(defn validate-message [params]
  (first
    (b/validate
      params
      :name v/required
      :imdb_link v/required
      :description [v/required [v/min-count 10]])))

(defn create-movie! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (redirect "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-movie! (assoc params))
      (redirect "/"))))

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (merge {:movies (db/get-movies)}
          (select-keys flash [:name :imdb_link :description :errors]))
   :name (:name flash)))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/" request (create-movie! request))
  (GET "/about" [] (about-page)))


