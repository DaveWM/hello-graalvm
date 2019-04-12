(ns hello-graalvm.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (org.graalvm.polyglot Context Value Source))
  (:gen-class))

(def permitted-languages ["R"])

(def context (-> (Context/newBuilder (into-array permitted-languages))
                 (.allowAllAccess true)
                 (.build)))


(defn execute-script [filename args]
  (let [source-file (io/file (io/resource filename))
        source-language (Source/findLanguage source-file)
        source-script (-> (Source/newBuilder source-language source-file)
                          (.build))]
    (-> (.eval context source-script)
        (.execute (into-array args)))))


(defn -main
  [& args]
  (println "Hello Grallvm!")
  (-> (execute-script "example.R" [10001 10001])
      (.asString)))


#_(-main)
