(ns schema.contrib.number-test
  (:require [clojure.test :refer :all]
            [schema.core :refer [check]]
            [schema.contrib.number :refer [gt lt between]]
            [schema.contrib.human :refer [human-explain]]))

(deftest number-test-unit
  (testing "lt"
    (is (nil? (check (lt 1) 0.5)))
    (is (= "1.5 is not lt 1"
           (human-explain (check (lt 1) 1.5)))))

  (testing "gt"
    (is (nil? (check (gt 1) 1.5)))
    (is (= "0.5 is not gt 1"
           (human-explain (check (gt 1) 0.5)))))

  (testing "between"
    (is (nil? (check (between 1 5) 3)))
    (is (= "7 is not between 1 and 5"
           (human-explain (check (between 1 5) 7))))))


