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
  get-content
  [page-id]
  (first (jdbc/query DB ["SELECT * FROM page_content WHERE id_page = ?" page-id])))

(defn
  query-pages
  []
  (jdbc/query DB
              ["select p.*, parent.urn as parent_urn from pages as p left join pages as parent on p.parent = parent.id"]
              ; I would like to mark clob fields on vector
              :row-fn (fn [rs]
                        (-> rs
                            (assoc :title (clob-to-string (:title rs)))
                            (assoc :content-fn (fn [] (:content (get-content (:id rs)))))))))
(defn
  insert-page!
  [{content :content :as data}]
  (jdbc/with-db-transaction [trans-conn DB]
    ; Insert page
    (let [page-id (-> (jdbc/insert! trans-conn :pages (dissoc data :content))
                      first
                      vals
                      first)]
      ; insert page contents or empty
      (jdbc/insert! trans-conn :page_content {:id_page page-id :content (or content "")})
      page-id)))

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
  (jdbc/update! DB :page_content {:content content} ["id_page = ?" id]))
