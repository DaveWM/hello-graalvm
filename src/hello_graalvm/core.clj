(ns hello-graalvm.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (org.graalvm.polyglot Context Value Source))
  (:gen-class))

(def permitted-languages ["R"])

(def context (-> (Context/newBuilder (into-array permitted-languages))
                 (.allowAllAccess true)
                 (.build)))

;; Read R source from file
#_(defn execute-script [filename args]
  (let [source-file (io/file (io/resource filename))
        source-language (Source/findLanguage source-file)
        source-script (-> (Source/newBuilder source-language source-file)
                          (.build))]
    (-> (.eval context source-script)
        (.execute (into-array args)))))

;; Read R source from raw string
(defn execute-script [filename args]
  (let [source-script "require(dplyr)
require(tibble)

function(value_in, date_in) {
    if (length(which(!is.na(value_in))) == 0)
    {'pass'}
    else {
        nrow <- tibble(value = value_in %>% replace(.,is.na(.),0),
                       date = date_in) %>%
            filter(is.na(date))
        
        ifelse(sum(nrow$value,na.rm=T)>=100,'fail','pass')
    } 
}" ]
    (-> (.eval context "R" source-script )
        (.execute (into-array args)))))


(defn -main
  [& args]
  (println "Hello Grallvm!")
  (-> (execute-script "example.R" [10001 10001])
      (.asString)
      (println)))


#_(-main)
