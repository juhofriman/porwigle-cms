(ns user
  (require [clojure.tools.namespace.repl :refer [refresh refresh-all]])
  (require [porwigle.core :refer :all])
  (require [porwigle.server :as server]))


(def porwigle-instance nil)

(defn
  init
  "Constructs the current development system."
  []
  (alter-var-root #'porwigle-instance
    (constantly (server/porwigle-instance))))

(defn
  start
  "Starts the current development system."
  []
  (alter-var-root #'porwigle-instance server/start-porwigle))


(defn
  stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'porwigle-instance
    (fn [s] (when s (server/stop-porwigle s)))))

(defn
  go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn
  reset []
  (stop)
  (refresh-all :after 'user/go))
