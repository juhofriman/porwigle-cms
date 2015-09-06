(ns porwigle.core-test
  (:require [clojure.test :refer :all]
            [porwigle.core :refer :all]))

;; It seems quite hard to set up h2 in in-memory database during test
;; because once created tables it's gone

(def TEST-DB {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "db/test-db"})

; each test creates tables and drops them
; this fixture also redefs db connection params
(defn db-test-fixture [f]
  (with-redefs [DB TEST-DB]
    (create-tables)
    (f)
    (drop-tables)))

(use-fixtures :each db-test-fixture)

(deftest porwigle-persistence-tests-insert-page
  (testing "when inserting and getting root field are as expected"
    (do
      ; Insert something to root
      (insert-page! {:content "content" :title "my root title" :urn "/"})

      ; Assert page structure fields are as expected
      (let [{saved-title   :title
             saved-content :content
             saved-urn     :urn} (pagestructure "/")]

        (is (= "my root title" saved-title))
        (is (= "content" saved-content))
        (is (= "/" saved-urn))))))

(deftest porwigle-persistence-tests-update-content
  (testing "updating content"

    (let [; insert page
          id (insert-page! {:content "original content" :title "my root title" :urn "/"})
          ; update with given id
          u (update-content! id "updated content")
          ; retrieve page
          {updated-content :content} (pagestructure "/")]

        (is (not (nil? id)))
        (is (= "updated content" updated-content)))))

(deftest porwigle-persistence-tests-get-pagestructure
  ; insert root
  (let [root-id (insert-page! {:content "content" :title "my root title" :urn "/"})]
    (do
      ; insert some children to root
      (insert-page! {:content "content" :title "subpage 1" :urn "/sub" :parent root-id})
      (insert-page! {:content "content" :title "subpage 2" :urn "/sub2" :parent root-id})
      (insert-page! {:content "content" :title "subpage 3" :urn "/sub3" :parent root-id})

      ; assert page structure is as expected
      (let [{children-of-root :children} (pagestructure "/")]
        ; should have three children
        (is (= 3 (count children-of-root)))
        ; assert all subpage titles can are found
        (is (< -1 (.indexOf (map :title children-of-root) "subpage 1")))
        (is (< -1 (.indexOf (map :title children-of-root) "subpage 2")))
        (is (< -1 (.indexOf (map :title children-of-root) "subpage 3")))))))





