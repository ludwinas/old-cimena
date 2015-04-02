(ns cimena.routes.home
  (:require [cimena.layout :as layout]
            [cimena.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [ring.util.response :refer [redirect response]]))

(defn validate-message [params]
  (first
   (b/validate
    params
    :title v/required
    :link v/required)))

(defn save-movie! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (redirect "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-movie! (assoc params :is_watched false))
      (redirect "/"))))

(defn delete-movie! [{:keys [params]}]
  (do
    (db/delete-movie! {:id (Integer/parseInt (:id params))}))
  (response {:status "OK"}))

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (merge {:movies (db/get-movies)}
          (select-keys flash [:title :link :errors]))
   :name (:name flash)))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/" request (save-movie! request))
  (POST "/movie/delete" request (delete-movie! request))
  (GET "/about" [] (about-page)))
