(defproject advent-calendar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-fuzzy "0.4.1"]
                 [clj-time "0.15.0"]
                 [quil "2.8.0"]
                 ]
  :main ^:skip-aot advent-calendar.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
