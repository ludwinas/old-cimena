(ns cimena.routes.movie-search
  (:require [cimena.layout :as layout]
            [cimena.db.core :as db]
            [cimena.db.helpers :as dbh]
            [cimena.lib.util :as util]
            [cimena.config :as config]
            [compojure.core :refer [defroutes GET POST]]
            [taoensso.timbre :as timbre]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            [ring.util.response :refer [redirect response]]))

(timbre/refer-timbre) ;; provides timbre aliases in this ns

(defn get-api-key-from-config []
  (config/get-param-by-keyword :tmdb_api_key))

(defn search-tmdb-with-keyword [keyword]
  (let [tmdb-api-key (get-api-key-from-config)
        response (client/get "http://api.themoviedb.org/3/search/movie"
                             {:query-params {:api_key tmdb-api-key
                                             :query keyword}})
        decoded-response (parse-string (:body response) true)]
    (:results decoded-response)))

(defn movie-search-page [{:keys [params flash]}]
  (layout/render "movie-search.html" flash))

(defn movie-search-by-keyword  [{:keys [params]}]
  (let [keyword (:keyword params)
        results (search-tmdb-with-keyword keyword)]
    (-> (redirect "/movie-search")
        (assoc :flash {:keyword keyword :results results}))))

(defn movie-view-details [{:keys [params]}]
  (let [tmdb-api-key (get-api-key-from-config)
        tmdb-id (:id params)
        response (client/get (str "http://api.themoviedb.org/3/movie/" tmdb-id) 
                             {:query-params {:api_key tmdb-api-key}})
        decoded-response (parse-string (:body response) true)]
    (layout/render "movie-search-details.html" {:result decoded-response})
    ))

(defroutes movie-search-routes
  (GET "/movie-search" request (movie-search-page request))
  (GET "/movie-search/:id" request (movie-view-details request))
  (POST "/movie-search" request (movie-search-by-keyword request)))
