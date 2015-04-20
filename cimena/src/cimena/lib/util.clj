(ns cimena.lib.util)

(defn not-nil? [c]
  (not
   (nil? c)))

(defn int-or-nil [x]
  (if (integer? x)
    x
    (try (Integer/parseInt x)
         (catch NumberFormatException e nil))))
