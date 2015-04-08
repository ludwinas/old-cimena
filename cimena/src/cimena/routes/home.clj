(ns cimena.routes.home
  (:require [cimena.layout :as layout]
            [cimena.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [redirect response]]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

(defn checked? [c]
  (not
   (nil? c)))

(defn validate-message [params]
  (first
   (b/validate
    params
    :title v/required
    :link v/required)))

(defn save-movie! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (redirect "/")
        (assoc :flash (assoc params :errors (vals errors))))
    (do
      (let [query-params
            (assoc (select-keys params [:title :link])
                   :is_watched (checked? (:is_watched params)))]
      (db/create-movie! query-params))
      (redirect "/"))))

(defn delete-movie! [{:keys [params]}]
  (do
    (db/delete-movie! {:id (Integer/parseInt (:id params))}))
  (response {:status "OK"}))

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (merge {:movies (db/get-movies)}
          (select-keys flash [:title :link :is_watched :errors]))
   :name (:name flash)))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/" request (save-movie! request))
  (POST "/movie/delete" request (delete-movie! request))
  (GET "/about" [] (about-page)))
