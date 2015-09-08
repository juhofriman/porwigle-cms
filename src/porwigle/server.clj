(ns porwigle.server
  (:gen-class)
  (:require [org.httpkit.server :as srv]
            [clojure.data.json :as json]
            [porwigle.core :as porwigle]))

(defn my-value-reader [key value]
  (if (= (class value) java.sql.Timestamp)
    (.format (java.text.SimpleDateFormat. "d.M.yyyy HH:MM") value)
    value))

(defn
  handle-api-call
  [requri]
  (cond
   (.endsWith requri "_api/structure")
     {:status  200
      :headers {"Content-Type" "application/json"}
      :body (json/write-str (porwigle/pagestructure "/")
                            :value-fn my-value-reader)}
   (.endsWith requri "_api/templates")
     {:status  200
      :headers {"Content-Type" "application/json"}
      :body (json/write-str (porwigle/templates)
                            :value-fn my-value-reader)}))

(defn
  porwigle-request-handler
  [{requri :uri :as req}]
  (cond
   (= "/favicon.ico" requri)
     nil
   (.startsWith requri "/_api")
     (handle-api-call requri)
    :default
     {:status  200
      :headers {"Content-Type" "text/html"}
      :body (porwigle/eval-page (porwigle/pagestructure requri))}))

(defn
  start-porwigle
  [porwigle-instance]
  (when-not (nil? porwigle-instance)
    (assoc
      porwigle-instance
      :server
      (srv/run-server
       (:handler porwigle-instance)
       {:port (:port porwigle-instance) }))))

(defn
  stop-porwigle
  [porwigle-instance]
  (when-not (nil? (:server porwigle-instance))
    ((:server porwigle-instance) :timeout 100)
    (assoc porwigle-instance :server nil)))


(defn
  porwigle-instance
  "Returns a new instance of the porwigle-cms"
  []
  { :handler porwigle-request-handler
    :port 8081
    :server nil })
