(ns cimena.lib.util
  (:require [clojure.data]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(defn not-nil? [c]
  (not
   (nil? c)))

(defn int-or-nil [x]
  (if (integer? x)
    x
    (try (Integer/parseInt x)
         (catch NumberFormatException e nil))))

(defn set-difference [coll1 coll2]
  (let [first-set (into #{} coll1)
        second-set (into #{} coll2)]
        (clojure.set/difference first-set second-set)))

(defn get-item-with-keyword [keyword id coll]
  (some #(when (= id (keyword %)) %)
        coll))

(defn data-diff [coll1 coll2]
  "expects two list of integers, normalizes to int before comparing"
  (let [first-set (set (map int-or-nil coll1))
        second-set (set (map int-or-nil coll2))]
    (clojure.data/diff first-set second-set)))

(defn into-a-vec [item]
  "transforms the given item into a vector, no matter if it's a collection or a
  single item"
  (if (coll? item)
    (vec item)
    [item]))
