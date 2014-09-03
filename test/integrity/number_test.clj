(ns integrity.number-test
  (:require [clojure.test :refer :all]
            [schema.core :refer [check]]
            [integrity.number :refer [gt gte lt lte between]]
            [integrity.human :refer [human-walker]]))

(defn is-valid [test-schema val]
  (is (nil? ((human-walker test-schema) val))))

(defn is-invalid [test-schema val err]
  (is (= err ((human-walker test-schema) val))))

(deftest test-lt
  (doto (lt 1)
    (is-valid 0.9)
    (is-invalid 1 "1 is not lt 1")))

(deftest test-lte
  (doto (lte 1)
    (is-valid 1)
    (is-invalid 1.5 "1.5 is not lte 1")))

(deftest test-gte
  (doto (gte 1)
    (is-valid 1)
    (is-invalid 0.9 "0.9 is not gte 1")))

(deftest test-between
  (doto (between 1 5)
    (is-valid 1.1)
    (is-valid 4.9)
    (is-invalid 1 "1 is not between 1 and 5")
    (is-invalid 5 "5 is not between 1 and 5")))

(test-between)
