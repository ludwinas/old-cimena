(ns cimena.db.core
  (:require
    [yesql.core :refer [defqueries]]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/cimena_db"
   :user "cimena"
   :password "cimena"})

(defqueries "sql/queries.sql" {:connection db-spec})
