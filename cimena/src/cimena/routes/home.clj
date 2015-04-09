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

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (merge {:movies (db/get-movies)}
          (select-keys flash [:title :link :is_watched]))
   :name (:name flash)))

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

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/movie/:id" request (edit-movie request))
  (POST "/movie/:id" request (save-movie! request))
  (POST "/movie/:id/delete" request (delete-movie! request))
  (GET "/about" [] (about-page)))
