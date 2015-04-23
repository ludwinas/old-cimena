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

(defn validate-movie-tag [params]
  (first
   (b/validate
    params
    :label v/required
    :color v/required)))

(defn list-movie-tags [{:keys [params]}]
  (layout/render "movie-tag.html"
                 {:movie-tags [] }))

(defn save-movie-tag! [{:keys [params]}]
  (if-let [errors (validate-movie-tag params)]
    (-> (redirect (str "/movie-tag/" (:id params))) ;; redirect back to the same page
        (assoc :flash (assoc params :errors (vals errors))))
    (do
      (info params)
      ;; store the sumbitted data
      ;; (insert-movie-tag!)
      ;; and eventually decide whether it's an insert or an update
      ;; come to think of it I should offload the decision to a db helper
      (redirect "/movie-tag"))))

(defn edit-movie-tag [{:keys [params flash]}]
  (info flash)
  (layout/render "movie-tag-edit.html"
                 (merge
                  {:movie-tag [] }
                  (select-keys flash [:errors]))))

(defroutes movie-tag-routes
  (GET "/movie-tag" request (list-movie-tags request))
  (GET "/movie-tag/:id" request (edit-movie-tag request))
  (POST "/movie-tag/:id" request (save-movie-tag! request)))
