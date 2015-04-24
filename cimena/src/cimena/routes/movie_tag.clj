(ns cimena.routes.movie-tag
  (:require [cimena.layout :as layout]
            [cimena.db.core :as db]
            [cimena.db.helpers :as dbh]
            [cimena.lib.util :as util]
            [compojure.core :refer [defroutes GET POST]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [redirect response]]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

;; TODO: validate the color parameter to be a hex value. I'm sure there's a regex
;; for this
(defn validate-movie-tag [params]
  (first
   (b/validate
    params
    :label v/required
    :color v/required)))

(defn list-movie-tags [{:keys [params]}]
  (let [movie-tags (db/get-movie-tags)]
    (layout/render "movie-tag.html"
                   {:movie-tags movie-tags })))

(defn save-movie-tag! [{:keys [params]}]
  (if-let [errors (validate-movie-tag params)]
    (-> (redirect (str "/movie-tag/" (:id params))) ;; redirect back to the same page
        (assoc :flash (assoc params :errors (vals errors))))
    (do
      (dbh/store-movie-tag! params)
      (redirect "/movie-tag"))))

(defn edit-movie-tag [{:keys [params flash]}]
  (let [movie-tag (dbh/movie-tag-or-nil (:id params))]
    (layout/render "movie-tag-edit.html"
                   (merge
                    {:movie-tag movie-tag}
                    (select-keys flash [:errors])))))

(defn delete-movie-tag! [{:keys [params flash]}]
  (dbh/delete-movie-tag! (:id params))
  (response {:status "OK"}))

(defroutes movie-tag-routes
  (GET "/movie-tag" request (list-movie-tags request))
  (GET "/movie-tag/:id" request (edit-movie-tag request))
  (POST "/movie-tag/:id" request (save-movie-tag! request))
  (POST "/movie-tag/:id/delete" request (delete-movie-tag! request)))
