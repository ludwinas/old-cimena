(ns cimena.routes.movie-search
  (:require [cimena.layout :as layout]
            [cimena.db.core :as db]
            [cimena.db.helpers :as dbh]
            [cimena.lib.util :as util]
            [compojure.core :refer [defroutes GET POST]]
            [taoensso.timbre :as timbre]
            [ring.util.response :refer [redirect response]]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

 
(defn movie-search-page [{:keys [params flash]}]
  (layout/render "movie-search.html" flash))

(defn movie-search-by-keyword  [{:keys [params]}]
  ;; I have to now populate the results vector with actual results
  (let [keyword (:keyword params)
        results []]
    (-> (redirect "/movie-search")
        (assoc :flash {:keyword keyword :results results}))))

(defroutes movie-search-routes
  (GET "/movie-search" request (movie-search-page request))
  (POST "/movie-search" request (movie-search-by-keyword request)))
