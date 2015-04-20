(ns cimena.lib.util)

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
