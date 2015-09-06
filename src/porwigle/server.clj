(ns porwigle.server
  (:gen-class)
  (:require [org.httpkit.server :as srv]
            [porwigle.core :as porwigle]))

(defn
  porwigle-request-handler
  [req]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    (cond
             (= "/favicon.ico" (:uri req))
               nil
             :default
               (porwigle/eval-page (porwigle/pagestructure (:uri req))))})

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
