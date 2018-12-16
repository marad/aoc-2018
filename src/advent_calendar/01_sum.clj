(ns advent-calendar.01-sum
    (:require [clojure.string :as s]))

;; Input is a list of frequency changes as numbers
;; Output is a sum of them
;; Example: +1 -2 +3 +1
(defn puzzle-01 [input]
  (apply + input))
