(ns storm-example.core
  (:import [com.amazonaws.regions Regions])
  (:import [com.amazonaws ClientConfiguration])
  (:import [com.amazonaws.services.kinesis.stormspout KinesisSpoutConfig IKinesisRecordScheme])
  (:import [com.amazonaws.services.kinesis.stormspout InitialPositionInStream])
  (:import [com.amazonaws.services.kinesis.stormspout KinesisSpout])
  (:import [com.amazonaws.services.kinesis.stormspout KinesisSpoutConfig])
  (:import [com.amazonaws.auth.profile ProfileCredentialsProvider])
  (:import [backtype.storm.topology TopologyBuilder])
  (:import [com.amazonaws.auth
            AWSCredentialsProviderChain
            ClasspathPropertiesFileCredentialsProvider
            EnvironmentVariableCredentialsProvider
            SystemPropertiesCredentialsProvider])

  (:import [java.nio ByteBuffer])
  (:import [java.nio.charset CharacterCodingException Charset CharsetDecoder])

  (:import [backtype.storm StormSubmitter LocalCluster])
  (:import [backtype.storm.tuple Fields])
  (:use [backtype.storm clojure config])
  (:require [clj-json [core :as json]])
  (:gen-class))

(defn ->packet [tuple]
  (let [partition-key   (.getValueByField tuple "partition-key")
        sequence-number (.getValueByField tuple "sequence-number")
        record          (.getValueByField tuple "record-data")
        buffer          (ByteBuffer/wrap (.array record))
        decoder         (.newDecoder (Charset/forName "UTF-8"))
        data            (.toString (.decode decoder buffer))]
    {:partition-key partition-key :sequence-number sequence-number :record data }))

;; Used to convert Kinesis record into a tuple.
(defn record-scheme []
  (reify IKinesisRecordScheme
    (getOutputFields [this]
      (Fields. (into-array ["partition-key" "sequence-number" "record-data"])))
    (deserialize  [this record]
      [(.getPartitionKey record) (.getSequenceNumber record) (.getData record)])))

(defn kinesis-spout [stream-name zk-host-port]
  (let [client-config        (ClientConfiguration.)
        aws-creds            (ProfileCredentialsProvider.)
        kinesis-spout-config (doto (KinesisSpoutConfig. stream-name zk-host-port)
                               (.withKinesisRecordScheme (record-scheme))
                               (.withInitialPositionInStream InitialPositionInStream/TRIM_HORIZON)
                               (.withRegion Regions/AP_SOUTHEAST_1))]
    (KinesisSpout. kinesis-spout-config aws-creds client-config)))

(defbolt echo ["partition-key" "sequence-number" "record-data"] [tuple collector]
  (let [{:keys [partition-key sequence-number record] :as packet} (->packet tuple)]
    (emit-bolt! collector [partition-key sequence-number packet] :anchor tuple)
    (ack! collector tuple)))

(defn mk-topology []
  (topology
   { "kinesis-spout" (spout-spec (kinesis-spout "Foo" "localhost:2181") :p 1) }
   {
    "echo-bolt" (bolt-spec {"kinesis-spout" ["partition-key"]} echo :p 5)
    "simple-bolt" (shell-bolt-spec {"echo-bolt" :shuffle } ;; could be grouped based on guid or some other field
                                 "ruby"
                                 "simple.rb"
                                 ["record-data"]
                                 :p 1)
    }
   ))

(defn mk-shell-topology []
  (topology
   { "sentence-spout" (shell-spout-spec "ruby" "sentencespout.rb" ["col1"] :p 1) }
   { "split-bolt" (shell-bolt-spec { "sentence-spout" :shuffle }
                                   "ruby"
                                   "splitsentence.rb"
                                   ["token"]
                                   :p 1)
    }))

;; {TOPOLOGY-DEBUG true STORM-ZOOKEEPER-SERVERS "localhost" STORM-ZOOKEEPER-PORT "2181"}
(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "word-count" {TOPOLOGY-DEBUG true } (mk-topology))))

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
