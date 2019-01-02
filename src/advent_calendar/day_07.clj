(ns advent-calendar.day-07
    (:require [clojure.string :as s]
              [clojure.set :refer [difference union]]
              [com.rpl.specter :as sp]
              ))

(def input (advent-calendar.core/read-data "day7_preprocessed.txt"))
(def input ["C, A"
            "C, F"
            "A, B"
            "A, D"
            "B, E"
            "D, E"
            "F, E"
            ])

(def data (->> input
               (map #(s/split % #"\s*,\s*") )
               ;(map (juxt second first)) ;; Switch dependency direction
               ;(into {})
            ) )
(def ks (set (map first data)))
(def vs (set (map second data)))


(do
  (def tmp-data data)
  (defn step []
    ;; Find next step
    (def ks (set (map first tmp-data)))
    (def vs (set (map second tmp-data)))
    (def next-available-steps (difference ks vs))
    (def next-step (first (sort next-available-steps)))

    (println "Available steps:" next-available-steps)
    (println "Current step: " next-step)
    (println "----")

    (def tmp-data (filter (fn [[req target]] (not= next-step req)) tmp-data))
    next-step
    )


  ;; This solution misses the last STEP, add it manually
  (s/join (take 1000 (take-while #(not (nil? %)) (repeatedly step)))))

;; PART 2

(def workers 5)
(def step-base-duration 60)
(def steps "FHICMRTXYDBOAJNPWQGVZUEKLS")
(def steps "CABFDE")

(defn task-duration [step]
  (+ (- (int \A)) (int step) step-base-duration))


(def requirements (->> data
                       (group-by second)
                       (map (fn [[k v]] [(first k) (map ffirst v)]))
                       (into {})
                       ))

(def no-task \.)
(defn worker [id] {:id id :task no-task :work-left -1})

(def state
  {:second -1
   :tasks steps
   :finished #{}
   :workers (apply vector (for [id (range workers)] (worker id)))
   })

(def end-state
  {:second -1
   :tasks steps
   :finished #{}
   :workers [{:id 0 :task \C :work-left 0} (worker 1)]
   }
  )


(defn next-step [state]
  ;; Update finished tasks, free up workers
  ;; Determine next tasks and assign free workers
  ;; Pass time: increase timer, decrease tasks durations

  (->> state
       finish-tasks
       assign-tasks-to-workers
       progress-time))


(def simulation (iterate next-step state))

(->> (iterate next-step state)
     (println)
     ;(drop-while (comp not work-finished?))
     (take 18))

(every? (:finished state) (:tasks final))

(def WORKERS [:workers sp/ALL])
(def FINISHED-WORKERS [WORKERS #(zero? (:work-left %))])

(defn- print-state [state]
  (println (:second state) "\t"
           (s/join "\t" (map (comp str :task) (:workers state))) "\t"
           (:finished state))
  )

(defn- work-finished? [state]
  (every? (:finished state) (:tasks state)))

(defn- finish-tasks [state]
  (let [finished-tasks (sp/select [FINISHED-WORKERS :task] state)]
    (->> state
         (sp/transform [:finished] #(union % (set finished-tasks)))
         (sp/transform [FINISHED-WORKERS]
                       #(assoc % :task no-task :work-left -1))
         (sp/transform [:workers sp/ALL #(> (:work-left %) 0) :work-left] dec)
         )
    ))

(defn- progress-time [state]
  (->> state
       (sp/transform [:second] inc)
       ))

;; Given a state returns a new state with available tasks
;; assigned to free workers
(defn- assign-tasks-to-workers [state]
  (let [tasks-to-process (get-all-available-tasks state)
        workers (get-all-available-workers state)
        tasks-to-workers (map vector tasks-to-process workers)]
    (reduce assign-worker state tasks-to-workers)))

(defn- assign-worker [state [{:keys [task duration]} worker]]
  (sp/transform [:workers sp/ALL #(= (:id %) (:id worker))]
                         #(assoc %
                                 :task task
                                 :work-left duration)
                         state))

(defn- next-available-task [state]
  (let [next-task (first (:tasks state))
        reqs (requirements next-task)
        requirements-finished? (every? (:finished state) reqs)]
    (when requirements-finished?
      {:task next-task
       :duration (task-duration next-task)})))

(defn- get-all-available-tasks [state]
  (->> (:tasks state)
       ;; Remove finished
       (filter (comp not (:finished state)))
       ;; Remove in-progress
       (filter (comp not (set (sp/select [:workers sp/ALL :task] state))))
       (map #(assoc {}
                    :task %1
                    :duration (task-duration %1)
                    :finished? (every? (:finished state) (requirements %1))))
       (filter :finished?)
       (map #(select-keys % [:task :duration]))
       ))

(defn- get-all-available-workers [state]
  (->> state
       :workers
       (filter #(= no-task (:task %)))))


