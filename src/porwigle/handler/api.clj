(ns porwigle.handler.api
  (:gen-class)
  (require [porwigle.core :as porwigle]
           [porwigle.db.operations :as db-operations]
           [clojure.data.json :as json]))

(defn
  json-value-reader
  [key value]
  (cond
   (= (class value) java.sql.Timestamp)
    (.format (java.text.SimpleDateFormat. "d.M.yyyy HH:MM") value)
   ; todo: we really need to filter all lazy functions from data
   (fn? value)
     nil
   :default value))

(defn
  get-pagestructure
  []
  (let [pagestructure (porwigle/pagestructure "/")]
    {:status 200
     :body (json/write-str (dissoc pagestructure :content-fn)
                           :value-fn json-value-reader)
     :headers {"Content-type" "application/json"
               "Access-Control-Allow-Origin" "*"}}))

(defn
  get-templates
  []
  (let [templates (db-operations/templates)]
    {:status 200
     :body (json/write-str templates
                           :value-fn json-value-reader)
     :headers {"Content-type" "application/json"}}))

(defn
  get-content
  [id]
  (let [content (db-operations/get-content id)]
    {:status 200
     :body (json/write-str content
                           :value-fn json-value-reader)
     :headers {"Content-type" "application/json"}}))

(defn
  update-content
  [id content]
  (db-operations/update-content! id content)
  {:status 204})
