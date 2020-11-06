(ns storm-example.core
  (:import [backtype.storm.topology TopologyBuilder])
  (:import [java.nio ByteBuffer])
  (:import [java.nio.charset CharacterCodingException Charset CharsetDecoder])

  (:import [backtype.storm StormSubmitter LocalCluster])
  (:import [backtype.storm.tuple Fields])
  (:use [backtype.storm clojure config])
  (:require [clj-json [core :as json]])
  (:gen-class))

(defn tick? [tuple]
  (and (= (.getSourceComponent tuple) "")
       (= (.getSourceStreamId tuple) "")))

(defn mk-topology []
  (topology
   { "sentence-spout" (shell-spout-spec "ruby" "sentencespout.rb" ["sentence"] :p 1 :conf nil) }
   { "split-bolt" (shell-bolt-spec { "sentence-spout" :shuffle }
                                   "ruby"
                                   "splitsentence.rb"
                                   ["token"]
                                   :p 1
                                   :conf {"topology.tick.tuple.freq.secs" 10})

    }))

;; {TOPOLOGY-DEBUG true STORM-ZOOKEEPER-SERVERS "localhost" STORM-ZOOKEEPER-PORT "2181"}
(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "sample" {TOPOLOGY-DEBUG true} (mk-topology))))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
   name
   {TOPOLOGY-DEBUG true
    TOPOLOGY-WORKERS 3}
   (mk-topology)))

(defn -main
  ([]
   (run-local!))
  ([name]
   (submit-topology! name)))
