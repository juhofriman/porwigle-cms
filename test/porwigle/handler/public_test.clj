(ns porwigle.handler.public-test
  (:require [clojure.test :refer :all]
            [porwigle.handler.public :refer :all]))


(deftest public-request-handler

  (testing "When page is non existent 404 with empty body is expected"

    (with-redefs [porwigle.core/eval-page (fn [_] "")
                  porwigle.core/pagestructure (fn [_] {:content ""})]

      (let [response (handle-request {:uri "/non-existent"})]
        (is (= 404 (:status response)))
        (is (nil? (:body response))))))

  (testing "When page exists 200 with contents as body is expected"

    (with-redefs [porwigle.core/eval-page (fn [_] "body")
                  porwigle.core/pagestructure (fn [_] {:content "body"})]

      (let [response (handle-request {:uri "/existing"})]
        (is (= 200 (:status response)))
        (is (= "body" (:body response)))))))
