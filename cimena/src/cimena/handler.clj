(ns cimena.handler
  (:require [compojure.core :refer [defroutes routes]]
            [cimena.routes.home :refer [home-routes]]
            [cimena.routes.movie-tag :refer [movie-tag-routes]]
            [cimena.routes.movie-search :refer [movie-search-routes]]
            [cimena.middleware
             :refer [development-middleware production-middleware]]
            [cimena.session :as session]
            [cimena.config :as config]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cronj.core :as cronj]))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
  app is deployed as a servlet on
  an app server such as Tomcat
  put any initialization code here"
  []
  (timbre/set-config!
   [:appenders :rotor]
   {:min-level :info
    :enabled? true
    :async? false ; should be always false for rotor
    :max-message-per-msecs nil
    :fn rotor/appender-fn})

  (timbre/set-config!
   [:shared-appender-config :rotor]
   {:path "cimena.log" :max-size (* 512 1024) :backlog 10})

  ;; calling this loads the config file and sets it in an atom
  ;; accessible in the future by calling config/get-config
  (config/init-config)

  (if (env :dev) (parser/cache-off!))
  ;;start the expired session cleanup job
  (cronj/start! session/cleanup-job)
  (timbre/info "\n-=[ cimena started successfully"
               (when (env :dev) "using the development profile") "]=-"))

(defn destroy
  "destroy will be called when your application
  shuts down, put any clean up code here"
  []
  (timbre/info "cimena is shutting down...")
  (cronj/shutdown! session/cleanup-job)
  (timbre/info "shutdown complete!"))

(def app
  (-> (routes
       home-routes
       movie-tag-routes
       movie-search-routes
       base-routes)
      development-middleware
      production-middleware))
