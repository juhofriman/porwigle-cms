(ns porwigle.db.operations
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [porwigle.db.schema :refer [DB]]))

(defn clob-to-string [clob]
  "Turn an Clob into a String"
  (if (string? clob)
    clob
    (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
      (apply str (line-seq rdr)))))

(defn
  query-pages
  []
  (jdbc/query DB
              ["select p.*, parent.urn as parent_urn from pages as p left join pages as parent on p.parent = parent.id"]
              ; I would like to mark clob fields on vector
              :row-fn (fn [rs]
                        (-> rs
                            (assoc :title (clob-to-string (:title rs)))
                            (assoc :content (clob-to-string (:content rs)))))))
(defn
  insert-page!
  [data]
  (first (vals (first (jdbc/insert! DB :pages data)))))

(defn
  insert-template!
  [data]
  (first (vals (first (jdbc/insert! DB :templates data)))))

(defn
  templates
  []
  (jdbc/query DB ["SELECT * FROM templates"]))

(defn
  query-template
  [template-id]
  (first (jdbc/query DB ["SELECT * FROM templates WHERE id = ?" template-id])))

(defn
  update-content!
  [id content]
  (jdbc/update! DB :pages {:content content} ["id = ?" id]))
