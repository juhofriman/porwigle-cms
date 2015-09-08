(ns porwigle.core
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]
            [clostache.parser :as clostache]
            [java-jdbc.ddl :as ddl]))

(def DB {:classname "org.h2.Driver"
         :subprotocol "h2:file"
         :subname "db/my-webapp"})


(defn
  create-tables
  []
  (jdbc/db-do-commands
   DB
   (ddl/create-table :templates
                     [:id "bigint primary key auto_increment"]
                     [:content "varchar(10000) not null"])
   (ddl/create-table :pages
                      [:id "bigint primary key auto_increment"]
                      [:urn "varchar(255) not null"]
                      [:parent "bigint references pages(id)"]
                      [:id_template "bigint references templates(id)"]
                      [:title "varchar(500) not null"]
                      [:content "varchar(10000) not null"]
                      [:created "timestamp not null default NOW()"])))

(defn
  drop-tables
  []
  (jdbc/db-do-commands
   DB
   (ddl/drop-table :pages)
   (ddl/drop-table :templates)))

(defn
  insert-page!
  [data]
  (first (vals (first (jdbc/insert! DB :pages data)))))

(defn
  insert-template!
  [data]
  (first (vals (first (jdbc/insert! DB :templates data)))))

(defn
  query-template
  [template-id]
  (first (jdbc/query DB ["SELECT * FROM templates WHERE id = ?" template-id])))

(defn
  update-content!
  [id content]
  (jdbc/update! DB :pages {:content content} ["id = ?" id]))

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


(defn clob-to-string [clob]
  "Turn an Clob into a String"
  (if (string? clob)
    clob
    (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
      (apply str (line-seq rdr)))))

(defn
  pagestructure
  [urn]
  (let [unsortedpages
        (jdbc/query DB
                    ["select p.*, parent.urn as parent_urn from pages as p left join pages as parent on p.parent = parent.id"]
                    ; I would like to mark clob fields on vector
                    :row-fn (fn [rs]
                              (-> rs
                                  (assoc :title (clob-to-string (:title rs)))
                                  (assoc :content (clob-to-string (:content rs))))))]
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
     (:content (query-template id_template))
     (assoc render-attrs :content content))))

(defn
  eval-page
  [page]
  (let [render-attrs (render-attrs-of-page page)]
    (clostache/render (apply-template page render-attrs) render-attrs)))






