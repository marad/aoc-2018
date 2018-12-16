(ns advent-calendar.core
    (:require [clojure.string :as s])
  (:gen-class))


(defn read-data [file]
  (as-> file _
        (slurp _)
        (s/split _ #"\n")))

(defn read-day1-data []
  (->> (read-data "input.txt")
       (map read-string)))

(defn read-day2-data [] (read-data "day2.txt"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
