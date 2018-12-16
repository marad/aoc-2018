(ns advent-calendar.02-repeating)

(comment

  (def input (advent-calendar.core/read-data "input.txt"))
  (def input [1 -2 3 1]) ; 2
  (def input [1 -1]) ; 0
  (def input [3 3 4 -2 -4])
  (def input [-6 3 8 5 -6])
  (def input [7 7 2 -7 -4])
  )

;; Task is to find out the repeating frequency by applying frequency changes

(defn mark-seen [value]
  (swap! seen #(conj % value))
  value)

(defn wasnt-seen? [value]
  (let [not-seen? (not (@seen value))]
    (mark-seen value)
    (when-not not-seen? (println value))
    not-seen?))

(defn puzzle-02 [input]
  (def seen (atom #{}))
  (doall (as-> input _
              (cycle _)
              (map #(partial + %) _)
              (reductions (fn [v f] (f v)) 0 _)
              (take-while wasnt-seen? _)
              (take 100000000 _)
              ))
  nil)


