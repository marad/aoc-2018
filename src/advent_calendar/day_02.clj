(ns advent-calendar.day-02
    (require [advent-calendar.core :refer [read-day2-data]]
             [clojure.set :refer [union]]
             [clj-fuzzy.metrics :refer [levenshtein]]
             ))

(comment

  (def input (read-day2-data))
  (def input ["abcdef" "bababf" "abbcdf" "abcccf" "aabcdf" "abcdef" "ababaf"])
  (def input ["abcde" "fghij" "klmno" "pqrst" "fguij" "axcye" "wvxyz"])

  )

(defn checksum [box-ids]
  (let [id-stats (fn [box-id]
                     (as-> box-id _
                           (frequencies _)
                           (reduce-kv #(assoc %1 %3 1) {} _)
                           (select-keys _ [3 2])))
        summary (as-> input _
                      (map stats _)
                      (apply merge-with + _))
        two-times (summary 2)
        three-times (summary 3)]
    (* two-times three-times)
    ))



(def result (similar-box-ids input))
(defn similar-box-ids [box-ids]
  (->>
    (for [x box-ids y box-ids :when (> (compare  x y) 0)] {:pair [x y] :distance (levenshtein x y) })
    (filter #(= 1 (%1 :distance)))
    (map :pair)
    ))

