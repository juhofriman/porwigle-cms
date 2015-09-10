(ns user
  (require [clojure.tools.namespace.repl :refer [refresh refresh-all]])
  (require [porwigle.db.schema :as schema])
  (require [porwigle.db.operations :as db-operations])
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
    (try (schema/drop-tables) (catch Exception e)) ; tables did not exist
    (schema/create-tables)
    (let [bootstrap-template-id (db-operations/insert-template! {:content (slurp-resource "test-site-html/bootstrap-template.html")
                                                   :title "bootstrap template"})
          plain-template-id (db-operations/insert-template! {:content (slurp-resource "test-site-html/plain-template.html")
                                               :title "plain template"})
          root-id (db-operations/insert-page! { :urn "/"
                                  :title "Root page"
                                  :id_template bootstrap-template-id
                                  :content (slurp-resource "test-site-html/root.html")})]

      (db-operations/insert-page! { :urn "/subpage1"
                      :parent root-id
                      :id_template plain-template-id
                      :title "Subpage one"
                      :content (slurp-resource "test-site-html/subpage1.html")})
      (db-operations/insert-page! { :urn "/subpage2"
                      :parent root-id
                      :id_template plain-template-id
                      :title "Subpage 2"
                      :content (slurp-resource "test-site-html/subpage2.html")})
      (db-operations/insert-page! { :urn "/subpage3"
                      :parent root-id
                      :id_template bootstrap-template-id
                      :title "Subpage 3"
                      :content (slurp-resource "test-site-html/subpage3.html")}))))
