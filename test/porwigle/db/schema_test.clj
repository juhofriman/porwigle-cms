(ns porwigle.db.schema-test
  (:require [clojure.test :refer :all]
            [porwigle.db.schema :refer :all]))

(def TEST-DB {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "db/test-db"})

(defn db-test-fixture [f]
  (with-redefs [DB TEST-DB]
    (f)))

(use-fixtures :each db-test-fixture)

(deftest db-schema-test
  (testing "asserting create-drop works without exceptions"
    (do
      (create-tables)
      (drop-tables))))
