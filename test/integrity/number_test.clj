(ns integrity.number-test
  (:require [clojure.test :refer :all]
            [schema.core :refer [check]]
            [integrity.number :refer [gt gte lt lte between]]
            [integrity.human :refer [human-explain]]))

(deftest number-test-unit
  (testing "lt"
    (is (nil? (check (lt 1) 0.5)))
    (is (= "1.5 is not lt 1"
           (human-explain (check (lt 1) 1.5)))))

  (testing "lte"
    (is (nil? (check (lte 1) 1)))
    (is (= "1.5 is not lte 1"
           (human-explain (check (lte 1) 1.5)))))

  (testing "gt"
    (is (nil? (check (gt 1) 1.5)))
    (is (= "0.5 is not gt 1"
           (human-explain (check (gt 1) 0.5)))))

  (testing "gte"
    (is (nil? (check (gte 1) 1)))
    (is (= "0.5 is not gte 1"
           (human-explain (check (gte 1) 0.5)))))

  (testing "between"
    (is (nil? (check (between 1 5) 3)))
    (is (= "7 is not between 1 and 5"
           (human-explain (check (between 1 5) 7))))))


