(ns porwigle.db.schema
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]
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
                     [:title "varchar(500) not null"]
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
