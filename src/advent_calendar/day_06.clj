(ns advent-calendar.day-06
    (:require [clojure.string :as s]
              [quil.core :as q]
              ))

(def input (advent-calendar.core/read-data "day6.txt"))

(->> input
     read-points
     )

(defn read-points [input]
  (->> input
       (map #(s/split % #"\s*,\s*"))
       (map #(map read-string %))
       (map #(zipmap [:x :y] %))
       )
  )

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
