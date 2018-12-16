(ns advent-calendar.day-04
    (:require [clojure.string :as s]
              [clj-time.core :as t]
              [clj-time.format :as tf]
              [clj-time.coerce :as tc]
              ))


(comment

  (def input [
              "[1518-11-01 00:00] Guard #10 begins shift"
              "[1518-11-01 00:05] falls asleep"
              "[1518-11-01 00:25] wakes up"
              "[1518-11-01 00:30] falls asleep"
              "[1518-11-01 00:55] wakes up"
              "[1518-11-01 23:58] Guard #99 begins shift"
              "[1518-11-02 00:40] falls asleep"
              "[1518-11-02 00:50] wakes up"
              "[1518-11-03 00:05] Guard #10 begins shift"
              "[1518-11-03 00:24] falls asleep"
              "[1518-11-03 00:29] wakes up"
              "[1518-11-04 00:02] Guard #99 begins shift"
              "[1518-11-04 00:36] falls asleep"
              "[1518-11-04 00:46] wakes up"
              "[1518-11-05 00:03] Guard #99 begins shift"
              "[1518-11-05 00:45] falls asleep"
              "[1518-11-05 00:55] wakes up"
              ])

  (def input (advent-calendar.core/read-data "day4.txt"))


  ## Zadanie
  Wybrać strażnika oraz minutę w której najprawdopodobnie będzie spał.


  ## Formaty danych

  Wpis logu:
  {:time #inst "1518-11-05 00:55"
   :log "wakes up"}

  Komplet danych dla strażnika:
  {:guard-id 123
   :sleep-times [#{1 2 3 4} #{30 31 32}]}

  ## Proces rozwiązania

  Dla każdego strażnika wyznaczę minutę w której najprawdopodobniej będzie spał razem
  z ilością dni, w których w tej minucie spał. Wybiorę strażnika, który najczęściej spał
  w swojej minucie.

  Dane nie są posortowane. Po wstępnym przeparsowaniu danych należy je posortować.
  Ostatecznie wystarczy mieć listę zbiorów minut, w których strażnik spał.
  Wtedy można utworzyć mapę [minuta -> ilość dni, w których spał w danej minucie]

  Kroki do rozwiązania:
  - Parsowanie i sortowanie logów
  - Agregacja logów per dzień dla strażnika
  - Agregacja danych per strażnik
  - Znalezienie najbardziej prawdopodobnej minuty snu dla strażnika
  - Wyznaczenie strażnika, który najczęściej spał w swojej minucie

  )


(defn solve [input]
  (let [{:keys [minute guard]} (->> input
                                    process-data
                                    ;(sort-by :time-asleep) ;; Pierwsza część
                                    (sort-by :count-asleep) ;; Druga część
                                    last)
        guard-id (read-string guard)]
    (* guard-id minute)))

(defn process-data [input]
  (->> input
       parse-and-sort-data
       group-logs-by-guard
       (map read-guard-logs)
       (map  #(merge (sleep-stats (:sleep-times %1)) %1))
       (map #(select-keys % [:minute :time-asleep :guard :count-asleep]))))

(def formatter (tf/formatter "yyyy-MM-dd HH:mm"))

(comment Parsowanie i sortowanie logów)

(defn parse-and-sort-data [input]
  (->> input
       (map #(let [groups (re-find #"\[(\d+-\d+-\d+ \d+:\d+)\] (.*)" %)]
               {:date (tf/parse formatter (nth groups 1))
                :log-message (nth groups 2)}
               ))
       (sort-by :date)
       (map #(assoc %1 :day (tc/to-local-date (:date %1))))
       ;(group-by :day)
       ))

(comment Agregacja logów dla strażnika

         Na wejściu dostajemy listę obiektów typu:
         {:date #clj-time/date-time "1518-11-01T00:00:00.000Z", :log-message "Guard #10 begins shift", :day #object[org.joda.time.LocalDate 0x2df872c 1518-11-01]}

         Musimy przejść po wszystkich logach i każdy oznaczyć identyfikatorem strażnika, którego opisuje (czyli ostatniego, który objął wartę).



         )

(defn group-logs-by-guard [logs]
  (->> logs
              (reduce
                (fn [{:keys [last-guard result]}
                     {:keys [log-message] :as log}]
                    (let [guard-id-from-message (second (re-find #"Guard #(\d+)" log-message))
                          current-guard (or guard-id-from-message last-guard)]
                      {:last-guard current-guard
                       :result (conj result (assoc log :guard current-guard))}
                      ))
                {:last-guard nil
                 :result []})
              :result
              (group-by :guard)
              (apply list)
              (map #(zipmap [:guard :logs] %))
              ))

(comment Wyznaczanie przedziałów czasu spania dla strażnika

         Wejście:
         {:guard "10", :logs [{:date #clj-time/date-time "1518-11-01T00:00:00.000Z", :log-message "Guard #10 begins shift", :day #object[org.joda.time.LocalDate 0x4fbf5d46 "1518-11-01"], :guard "10"}
                              {:date #clj-time/date-time "1518-11-01T00:05:00 .000Z", :log-message "falls asleep", :day #object[org.joda.time.LocalDate 0x78c40cb6 "1518-11-01"], :guard "10"}
                              {:date #clj-time/date-time "1518-11-01T00:25:00.000Z", :log-message "wakes up", :day #object[org.joda.time.LocalDate 0x5c2784 f4 "1518-11-01"], :guard "10"}
                              {:date #clj-time/date-time "1518-11-01T00:30:00.000Z", :log-message "falls asleep", :day #object[org.joda.time.LocalDate 0x656d033 "1518-11-01"], :guard "10"}
                              {:date #clj-time/date-time "1518-11-01T00:55:00.  000Z", :log-message "wakes up", :day #object[org.joda.time.LocalDate 0x3336e8d2 "1518-11-01"], :guard "10"}
                              {:date #clj-time/date-time "1518-11-03T00:05:00.000Z", :log-message "Guard #10 begins shift", :day #object[org.joda.time.LocalDate 0x3f3c4bda "1518-11-03"], :guard "10"}
                              {:date #clj-time/date-time "1518-11-03T00:24:00.000Z", :log-message "falls asleep", :day #object[org.joda.time.LocalDate 0x2b7a9d80 "1518-11-03"], :guard "10"}
                              {:date #clj-time/date-time "1518-11-03 T00:29:00.000Z", :log-message "wakes up", :day #object[org.joda.time.LocalDate 0x5ef0851a "1518-11-03"], :guard "10"}]}
         )

(defn read-guard-logs [guard-data]
  (let [sleep-times (->> guard-data
                         :logs
                         (filter #(not (s/starts-with? (:log-message %) "Guard") ))
                         (map :date)

                         (partition 2)
                         (map (fn [[from to]]
                                  (let [from-minute (t/minute from)
                                        to-minute (t/minute  to)]
                                    (range from-minute to-minute))))
                         (map set))]
    (assoc guard-data :sleep-times sleep-times)
    )
  )


(comment Wyznaczenie najbardziej "śpiącej" minuty strażnika na podstawie listy zbiorów minut snu

         (def sleep-times [#{12 13 14} #{14 15 16} #{13 14 15}])

         (def sleep-frequency-map
           (apply merge-with +
                  (map #(into {} (map (fn [x] [x 1]) %)) sleep-times)))

         Mając częstotliwość snu można wyznaczyć w której minucie strażnik spał najczęściej:

         (def guard-most-probable-minute
           (zipmap [:minute :count-asleep]
                   (reduce (fn [[acc-minute acc-count :as acc] [new-minute new-count :as new-val]]
                               (if (> new-count acc-count) new-val acc))
                           sleep-frequency-map)))
         )

(defn find-most-probable-sleep-minute [sleep-logs]
  (->> sleep-logs
       (map #(into {} (map (fn [x] [x 1]) %)))
       (apply merge-with +)
       (reduce (fn [[acc-minute acc-count :as acc] [new-minute new-count :as new-val]]
                   (if (> new-count acc-count) new-val acc)))
       (zipmap [:minute :count-asleep])
       ))

(defn minutes-asleep [sleep-logs]
  (->> sleep-logs
       (map count)
       (apply +)))

(defn sleep-stats [sleep-logs]
  (if-not (empty? sleep-logs)
          (let [best-minute (find-most-probable-sleep-minute sleep-logs)
                sleep-time (minutes-asleep sleep-logs)]
            (merge best-minute {:time-asleep sleep-time}))
          {:minute nil
           :count-asleep 0
           :time-asleep 0}
          )
  )

