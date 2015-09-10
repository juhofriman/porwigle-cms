(ns porwigle.server
  (:gen-class)
  (:require [org.httpkit.server :as srv]
            [compojure.route :as route]
            [compojure.handler :as compojure-handler] ; form, query params decode; cookie; session, etc
            [compojure.core :as compojure]
            [porwigle.handler.api :as api]
            [porwigle.handler.public :as public]))

(compojure/defroutes all-routes
  (route/resources "/admin")
  (compojure/GET "/_api/structure" [] (api/get-pagestructure))
  (compojure/GET "/_api/templates" [] (api/get-templates))
  (compojure/GET "/_api/content/:id" [id] (api/get-content id))
  (compojure/PUT "/_api/content/:id" [id content] (api/update-content id content))
  (compojure/GET "/*" request (public/handle-request request)))

(defn
  start-porwigle
  [porwigle-instance]
  (when-not (nil? porwigle-instance)
    (assoc
      porwigle-instance
      :server
      (srv/run-server
       (compojure-handler/site #'all-routes)
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
  {
    :port 8081
    :server nil })
