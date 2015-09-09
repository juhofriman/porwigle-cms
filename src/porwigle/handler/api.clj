(ns porwigle.handler.api
  (:gen-class)
  (require [porwigle.core :as porwigle]
           [clojure.data.json :as json]))

(defn
  json-value-reader
  [key value]
  (if (= (class value) java.sql.Timestamp)
    (.format (java.text.SimpleDateFormat. "d.M.yyyy HH:MM") value)
    value))

(defn
  get-pagestructure
  []
  (let [pagestructure (porwigle/pagestructure "/")]
    {:status 200
     :body (json/write-str pagestructure
                           :value-fn json-value-reader)
     :headers {"Content-type" "application/json"}}))

(defn
  get-templates
  []
  (let [templates (porwigle/templates)]
    {:status 200
     :body (json/write-str templates
                           :value-fn json-value-reader)
     :headers {"Content-type" "application/json"}}))

(defn
  handle-request
  [{uri :uri}]
  (cond
   (.endsWith uri "structure")
     (get-pagestructure)
   (.endsWith uri "templates")
     (get-templates)
   :default (throw (RuntimeException. "No matching clause"))))