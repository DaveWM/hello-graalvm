(ns hello-graalvm.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [jackdaw.streams :as j]
            [jackdaw.serdes.edn :as jse]
            [clojure.string :as s])
  (:import (org.graalvm.polyglot Context Value Source)
           [org.apache.kafka.common.serialization Serdes])
  (:gen-class))

(def permitted-languages ["R"])

(def source-script "
function(value_in, date_in) {
    if (length(which(!is.na(value_in))) == 0)
    {'pass'}
    else {
        nrow <- tibble(value = value_in %>% replace(.,is.na(.),0),
                       date = date_in) %>%
            filter(is.na(date))

        ifelse(sum(nrow$value,na.rm=T)>=100,'fail','pass')
    }
}")

(def context-builder
  (-> (Context/newBuilder (into-array permitted-languages))
      (.allowAllAccess true)))

(defn build-context
  ([] (build-context []))
  ([deps]
   (doto (-> context-builder
             (.build))
     (.eval "R" (s/join "\n" (map #(str "require(" % ")") deps))))))

(defn ->fn [r-code context]
  (let [r-fn (.eval context "R" r-code)]
    (fn [& args]
      (.asString (.execute r-fn (into-array args))))))


(defn topic-config
  "Takes a topic name and returns a topic configuration map, which may
  be used to create a topic or produce/consume records."
  [topic-name]
  {:topic-name topic-name
   :partition-count 1
   :replication-factor 1
   :key-serde (jse/serde)
   :value-serde (jse/serde)})


(defn app-config
  "Returns the application config."
  []
  {"application.id" "word-count-2"
   "bootstrap.servers" "192.168.17.2:9092"
   "cache.max.bytes.buffering" "0"})

(defn build-topology
  "Reads from a Kafka topic called `input`, logs the key and value,
  and writes these to a Kafka topic called `output`. Returns a
  topology builder."
  [builder]
  (-> (j/kstream builder (topic-config "input"))
      (j/peek (fn [[k v]]
                (time (let [r-fn (->fn source-script (build-context ["tibble" "dplyr"]))]
                        (-> (r-fn 10001 10001)
                            println)))))
      (j/to (topic-config "output")))
  builder)

(defn start-app
  "Starts the stream processing application."
  [app-config]
  (let [builder  (j/streams-builder)
        topology (build-topology builder)
        app      (j/kafka-streams topology app-config)]
    (j/start app)
    (println "pipe is up")
    app))

(defn stop-app
  "Stops the stream processing application."
  [app]
  (j/close app)
  (println "pipe is down"))


(defn -main
  [& args]
  (println "Hello Grallvm!")

  (try
    (let [r-fn (->fn "if {" (build-context))]
      (println (r-fn)))
    (catch Exception e (println e)))

  (let [r-fn (->fn source-script (build-context ["tibble" "dplyr"]))]
    (time (dotimes [n 100]
            (-> (r-fn 10001 10001)
                (println)))))

  (let [app (start-app (app-config))]

    (println app (.state app)))

  (doto (Thread. #(while true
                    (Thread/sleep 10000)))
    (.start)))

