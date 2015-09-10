(ns porwigle.core
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]
            [clostache.parser :as clostache]
            [java-jdbc.ddl :as ddl]
            [porwigle.db.operations :as db-operations]))

(def DB {:classname "org.h2.Driver"
         :subprotocol "h2:file"
         :subname "db/my-webapp"})


(defn
  get-children-for
  [id haystack]
  (filter #(= (:parent %) id) haystack))

(defn
  build-structure
  [{node-id :id node-urn :urn :as node} unsortedpages]
  (assoc node :children
    (map #(build-structure % unsortedpages)
         (get-children-for node-id unsortedpages))))

(defn
  get-node
  [urn unsortedpages]
  (first (filter #(= urn (:urn %)) unsortedpages)))



(defn
  pagestructure
  [urn]
  (let [unsortedpages (db-operations/query-pages)]
    (build-structure (get-node urn unsortedpages) unsortedpages)))




(defn
  render-attrs-of-page
  [page]
  (-> (select-keys page [:title :children :parent_urn])
      (assoc :children-sorted (sort-by :title (:children page)))
      (assoc :created (fn
                        ([] (str (:created page)))
                        ([dateformat] (.format (java.text.SimpleDateFormat. dateformat) (:created page)))))))

(defn
  apply-template
  [{id_template :id_template content :content} render-attrs]
  (if (nil? id_template)
    ; Page does not have a template, so content itself is template
    content
    ; Retrieve template and return content
    (clostache/render
     (:content (db-operations/query-template id_template))
     (assoc render-attrs :content content))))

(defn
  eval-page
  [page]
  (let [render-attrs (render-attrs-of-page page)]
    (clostache/render (apply-template page render-attrs) render-attrs)))






