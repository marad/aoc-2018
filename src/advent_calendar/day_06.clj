(ns advent-calendar.day-06
    (:require [clojure.string :as s]
              [quil.core :as q]
              [clojure.set :refer [union]]
              ))


(defn read-points [input]
  (as-> input _
       (map #(s/split % #"\s*,\s*") _)
       (map #(map read-string %) _)
       (map #(zipmap [:x :y] %) _)
       (map vector (take (count _) (iterate inc 1)) _)
       (map (fn [[id point]]
                (assoc point :id id)) _)
       )
  )

;; Read points, and generate them IDs
(as-> (advent-calendar.core/read-data "day6.txt") _
      (read-points _)
      (def points _)
      )

;; Prepare the fields (size determined by looking at input data)
(def row-size 400)
(def size (* row-size row-size))
(def data (int-array size -1)) ;; -1 means empty, 0 means more than one ID

;; For each field find the nearest point and
;; write its id
(doseq
  [x (range row-size)
   y (range row-size)]
  (let [target {:x x :y y}
        sorted (sort-points-by-distance target points manhattan-distance)
        nearest (first sorted)
        second-nearest (second sorted)
        mark (if (= (:dist nearest) (:dist second-nearest))
               0
               (:id nearest))
        index (+ x (* y row-size))]
    (aset-int data index mark)))

(defn sort-points-by-distance [target points dist-f]
  (->> points
       (map #(assoc %1 :dist (dist-f target %1)))
       (sort-by :dist)))

(defn manhattan-distance [{px :x py :y} {qx :x qy :y}]
  (+ (Math/abs (- px qx))
     (Math/abs (- py qy))))


;; Count frequencies for all points (field area)
(def areas (sort-by second (frequencies data)))

;; Determine which point id's are on the edges - those are infinite
;; and should be ignored
(def infinite (union
                (set (take row-size data))
                (set (take row-size (drop (* (- row-size 1) row-size) data)))
                (apply union (for [row (range row-size)]
                          (let [left-edge (+ 0 (* row row-size))
                                right-edge (+ row-size -1 (* row row-size))]
                            #{(aget data left-edge)
                              (aget data right-edge)})
                          ))
                ))


;; Filter areas to only show "not-infinite"
(->> areas
     (filter (fn [[id _]] (nil? (infinite id)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PART 2
(def distances (int-array size -1)) ;; -1 means empty

;; For each field find the nearest point and
;; write its id
(doseq
  [x (range row-size)
   y (range row-size)]
  (let [target {:x x :y y}
        sorted (sort-points-by-distance target points manhattan-distance)
        sum (apply + (map :dist sorted))
        index (+ x (* y row-size))]
    (aset-int distances index sum)))

;; Count the fields that have distance less than 10000
(->> distances
     (filter #(< % 10000))
     count)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Quil stuff

(defn setup  []
  (q/frame-rate 30)                    ;; Set framerate to 1 FPS
  (q/background 200))                 ;; Set the background colour to
;; a nice shade of grey.

(comment
  (q/stroke  (q/random 255))             ;; Set the stroke colour to a random grey
  (q/stroke-weight  (q/random 10))       ;; Set the stroke thickness randomly
  (q/fill  (q/random 255))               ;; Set the fill colour to a random grey

  (let  [diam  (q/random 100)             ;; Set the diameter to a value between 0 and 100
         x    (q/random  (q/width))       ;; Set the x coord randomly within the sketch
         y    (q/random  (q/height))]     ;; Set the y coord randomly within the sketch
    (q/ellipse x y diam diam)))

(defn draw  []
  (q/background 250)
  (q/stroke-weight 0)
  (q/stroke 200)
  (q/fill 200)
  (dorun (->> points
              (map (fn [{:keys [x y]}]
                       (q/ellipse x y 3 3)
                       ))
              ))
  )

(q/defsketch example                  ;; Define a new sketch named example
  :title "Oh so many grey circles"    ;; Set the title of the sketch
  :settings #(q/smooth 2)             ;; Turn on anti-aliasing
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size  [400 400])                    ;; You struggle to beat the golden ratio
