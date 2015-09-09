(ns porwigle.server
  (:gen-class)
  (:require [org.httpkit.server :as srv]
            [clojure.data.json :as json]
            [porwigle.handler.api :as api]
            [porwigle.handler.public :as public]))

(defn
  porwigle-request-handler
  [{requri :uri :as req}]
  (cond
   (= "/favicon.ico" requri)
     nil
   (.startsWith requri "/_api")
     (api/handle-request req)
    :default
     (public/handle-request req)))

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
