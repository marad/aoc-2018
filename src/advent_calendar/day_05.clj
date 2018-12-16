(ns advent-calendar.day-04
    (:require [clojure.string :as s]))

(def input "dabAcCaCBAcCcaDA")

(def input "aaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAA")
(def input "AAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaAa")


(def input (slurp "day5.txt"))

(def result (reduction input))

;; Function that reduces the polymer
(defn reduction [input]
  (->> input
       (apply list)
       (reduce alchemy-reduction nil)
       s/join
       s/trim
       ;count
       ))

(def output (for [ch upper]
                 (-> result
                     (s/replace (re-pattern (str ch)) "")
                     (s/replace (re-pattern (s/lower-case (str ch))) "")
                     reduction
                     count
                     )))

;; Shortest polymer after removing obstacles
(first (sort output))



(defn alchemy-reduction [acc b]
  (if (empty? acc)
    (list b)
    (let [a (last acc)]
      (if (should-reduce? a b)
        (drop-last acc)
        (concat acc (list b))
        ))))

(defn should-reduce? [a b]
  (and (= (s/lower-case a) (s/lower-case b))
               (not= a b)))

(def lower (map char (range (int \a) (int \z))))
(def upper (map char (range (int \A) (int \Z))))

(def lower-upper (for [x lower y upper :when (= (s/lower-case x) (s/lower-case y))] (str x y)))
(def upper-lower (for [x upper y lower :when (= (s/lower-case x) (s/lower-case y))] (str x y)))

(def patterns (map re-pattern (concat lower-upper upper-lower)))


(do
  (assert (= (reduction "aA") "") "Basic reduction")
  (assert (= (reduction "abBA") "") "#2")
  (assert (= (reduction "abAB") "abAB") "#3")
  (assert (= (reduction "aabAAB") "aabAAB") "#4")
  (assert (= (reduction "dabAcCaCBAcCcaDA") "dabCBAcaDA") "#5")
  (assert (= (reduction "aaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAA") "") "#6")
  )

