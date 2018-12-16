(ns advent-calendar.day-03
    (:require [clojure.string :as s]
              [clojure.set :refer [difference]]
              )
    )


(defn read-claim [claim-declaration]
  (let [result (re-find #"#(\d+) @ (\d+),(\d+): (\d+)x(\d+)" claim-declaration)]
    {:claim-id (read-string (nth result 1))
     :x (read-string (nth result 2))
     :y (read-string (nth result 3))
     :w (read-string (nth result 4))
     :h (read-string (nth result 5))}))

(defn read-data []
  (as-> "day3.txt" _
        (slurp _)
        (s/split _ #"\n")
        (map read-claim _)
        ))


(comment

  (def size 1000)
  (def input (map read-claim ["#1 @ 1,3: 4x4" "#2 @ 3,1: 4x4" "#3 @ 5,5: 2x2"]))
  (def input  (read-data))

  )


(defn draw-claim [{:keys [x y w h] :as claim} data]
  (for [cx (range x (+ x w))
        cy (range y (+ y h))]
       (let [index (+ cx (* size cy))
             current-val (aget data index)]
         (aset-int data index (inc current-val)))))

(defn count-conflicting [data]
  (count (filter #(>= % 2) data)))

(comment
  Prepare data table
  (def data (int-array (* size size) 0))

  Draw all claims rectangles on the data table
  (doall (for [claim input] (draw-claim claim data)))

  Count conflicting fields
  (println "Conflicting: " (count-conflicting data)))


;; Part 2
;; Find claim ID that does not conflict with anything

(defn colliding? [{ax :x ay :y aw :w ah :h :as a} {bx :x by :y bw :w bh :h :as b}]
  ;(< ax (+ bx bw))
  (and
    (< ax (+ bx bw))
    (< ay (+ by bh))
    (< bx (+ ax aw))
    (< by (+ ay ah))))

(let [dirty (atom #{})
      all (set (map :claim-id input))]
  (doall (for [{a-id :claim-id :as a} input
               {b-id :claim-id :as b} input
               :when (< a-id b-id)]
              (do
                ;(println [a b (colliding? a b)])
                (if (colliding? a b)
                  (swap! dirty #(conj % a-id b-id))))))
  (let [clean (difference all @dirty)]
    (println "Clean claims:" clean)))



