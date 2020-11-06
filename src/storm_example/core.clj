(ns storm-example.core
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:gen-class))

(def demo
  (topology
   {
    "sentence-spout"   (shell-spout-spec "ruby" "spouts/debug.rb" ["sentence"] :p 1)
    "name-spout"        (shell-spout-spec "ruby" "spouts/name.rb" ["id" "name"] :p 1)
    "nickname-spout"    (shell-spout-spec "ruby" "spouts/nickname.rb" ["id" "nickname"] :p 1)
    "multistream-spout" (shell-spout-spec "ruby" "spouts/multistream.rb" {"name-stream" ["id" "name"] "nickname-stream" ["id" "nickname"] } :p 1)
    }


   {
    "tick-bolt"
    (shell-bolt-spec {"sentence-spout" :shuffle } "ruby" "bolts/tick.rb" ["words"] :p 1 :conf {"topology.tick.tuple.freq.secs" 10})
    "words-bolt"       (shell-bolt-spec {"sentence-spout" :shuffle } "ruby" "bolts/split.rb" ["words"])
    "banner-bolt"        (shell-bolt-spec {"sentence-spout" :shuffle } "ruby" "bolts/banner.rb" ["banner"] :p 1)
    "capitalize-bolt"  (shell-bolt-spec {"words-bolt" :shuffle } "ruby" "bolts/capitalize.rb" ["capitalized"] :p 1)
    "count-bolt"       (shell-bolt-spec {"words-bolt" :shuffle } "ruby" "bolts/count.rb" ["count"] :p 1 )
    "word-bolt"        (shell-bolt-spec {"words-bolt" :shuffle } "ruby" "bolts/word.rb" ["word-out"] :p 1 )
    "freq-bolt"        (shell-bolt-spec {"word-bolt" ["word-out"] } "ruby" "bolts/freq.rb" ["freq-map"] :p 2 )
    "global-freq-bolt" (shell-bolt-spec {"word-bolt" :all } "ruby" "bolts/freq.rb" ["freq-map"] :p 2 )
    "multistream-bolt-shuffle" (shell-bolt-spec {["multistream-spout" "name-stream"] :shuffle ["multistream-spout" "nickname-stream"] :shuffle "sentence-spout" :shuffle}
                                  "ruby" "bolts/multistream.rb" ["multi"] :p 1 )

    "multistream-in-bolt-fields" (shell-bolt-spec {["multistream-spout" "name-stream"] ["id"] ["multistream-spout" "nickname-stream"] ["id"] "sentence-spout" :shuffle}
                                                  "ruby" "bolts/join.rb" ["multi"] :p 1 )

    "multistream-in-multistream-out-bolt-fields" (shell-bolt-spec {["multistream-spout" "name-stream"] ["id"]
                                                                   ["multistream-spout" "nickname-stream"] ["id"]
                                                                   "sentence-spout" :shuffle}
                                                                  "ruby" "bolts/multistream.rb" {"name-bolt-stream" ["name"]
                                                                                                "nickname-bolt-stream" ["nick"] } :p 1 )
    }))


;; {TOPOLOGY-DEBUG true STORM-ZOOKEEPER-SERVERS "localhost" STORM-ZOOKEEPER-PORT "2181"}
(defn run-local! [topology]
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "demo" {TOPOLOGY-DEBUG true} topology)))

(defn -main
  ([]
   (run-local! demo)))
