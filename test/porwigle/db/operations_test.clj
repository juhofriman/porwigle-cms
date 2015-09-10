(ns porwigle.db.operations-test
  (:require [clojure.test :refer :all]
            [porwigle.db.schema-test :refer [TEST-DB]]
            [porwigle.db.schema :refer :all]
            [porwigle.db.operations :refer :all]))

(defn db-test-fixture [f]
  (with-redefs [DB TEST-DB]
    (create-tables)
    (f)
    (drop-tables)))

(use-fixtures :each db-test-fixture)

(deftest template-tests

  (testing "after inserting template to empty db one is expected when queried"
    (do
      (insert-template! {:title "template" :content "content"})
      (is (= 1 (count (templates)))))))

(deftest page-tests

  (testing "after inserting page to empty db one is expected when queried"
    (do
      (insert-page! {:urn "/" :title "template" :content "content"})
      (let [pages (query-pages) page (first pages)]
        (is (= 1 (count pages)))
        (is (fn? (:content-fn page)))
        (is (= "content" ((:content-fn page))))))))
