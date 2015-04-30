(ns cimena.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [taoensso.timbre :as timbre]))

(defonce config-data (atom nil))

(def CONFIG-URI (io/resource "config/general.edn"))

(defn load-config [filename]
  (edn/read-string (slurp filename)))

(defn get-config []
  @config-data)

;; so here we're assuming that the config file is a hashmap
(defn get-param-by-keyword [keyword]
  "return the config parameter that corresponds to the given hashmap keyword"
    (keyword @config-data))

(defn init-config []
  (let [conf (load-config CONFIG-URI)]
    (reset! config-data conf)))
