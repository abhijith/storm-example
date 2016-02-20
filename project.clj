(defproject storm-example "0.1.0-SNAPSHOT"
  :resource-paths ["multilang"]
  :repositories {}
  :aot :all

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [commons-collections/commons-collections "3.2.1"]
                 [com.amazonaws/kinesis-storm-spout "1.1.0"]
                 [org.apache.storm/storm-core "0.10.0"]
                 [clj-json "0.5.3"]]

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[cider/cider-nrepl "0.11.0-SNAPSHOT"]
                                  [org.clojure/tools.nrepl "0.2.12"]]}
             }
  :main storm-example.core
  :min-lein-version "2.0.0")
