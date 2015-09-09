(ns porwigle.handler.api-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :refer [read-json]]
            [porwigle.handler.api :refer :all]))

(defn
  contains-header?
  [{headers :headers} k v]
  (and (contains? headers k) (= (get headers k) v)))

(defn
  dummy-pagestructure
  [_]
  {:title "testpage"
   :content "content"
   :created (new java.sql.Timestamp (System/currentTimeMillis))
   :children []
   :template nil})

(defn
  dummy-templates
  []
  [{:id 1
    :content "template content"
    :title "My template"}])

(deftest api-request-handler

  ; redef core functions dependent on
  (with-redefs [porwigle.core/pagestructure dummy-pagestructure
                porwigle.core/templates dummy-templates]

    (testing "Request to _api/structure should render page structure in json"

      (let [response (handle-request {:uri "_api/structure"})]
        ; Just assert body is json parseable
        (is (-> (read-json (:body response))
                nil?
                not))
        ; Simple assert that content is somewhat as expected
        (is (= "testpage" (:title (read-json (:body response)))))
        (is (= 200 (:status response)))
        (is (contains-header? response "Content-type" "application/json"))))

    (testing "Request to _api/templates should render templates in json"

      (let [response (handle-request {:uri "_api/templates"})]
        ; Just assert body is json parseable
        (is (-> (read-json (:body response))
                nil?
                not))
        ; Simple assert that content is somewhat as expected
        (is (= "My template" (:title (first (read-json (:body response))))))
        (is (= 200 (:status response)))
        (is (contains-header? response "Content-type" "application/json"))))))
