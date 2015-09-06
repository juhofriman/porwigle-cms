(ns user
  (require [clojure.tools.namespace.repl :refer [refresh refresh-all]])
  (require [porwigle.core :refer :all])
  (require [porwigle.server :as server])
  (require [clojure.java.io :as io]))


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

(defn
  slurp-resource
  [f]
  (slurp (io/file (io/resource f))))

(defn
  load-test-site
  []
  (do
    (drop-tables)
    (create-tables)
    (let [root-id (insert-page! { :urn "/"
                                  :title "Root page"
                                  :content (slurp-resource "test-site-html/root.html")})]

      (insert-page! { :urn "/subpage1"
                      :parent root-id
                      :title "Subpage one"
                      :content (slurp-resource "test-site-html/subpage1.html")})
      (insert-page! { :urn "/subpage2"
                      :parent root-id
                      :title "Subpage 2"
                      :content (slurp-resource "test-site-html/subpage2.html")}))))
