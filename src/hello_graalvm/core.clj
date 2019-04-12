(ns hello-graalvm.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (org.graalvm.polyglot Context Value TypeLiteral))
  (:gen-class))

(def context (-> (Context/newBuilder (into-array ["R"]))
                 (.allowAllAccess true)
                 (.build)))

(defn read-file [filename]
  (-> (io/resource filename)
      (slurp)))


(defn execute-script [script args]
  (-> (.eval context "R" script)
      (.execute (into-array args))
      (.asString)))


(defn run []
  (-> (read-file "example.R")
      (execute-script [10001 10001])))

(run)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
