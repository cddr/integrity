(ns schema.contrib.human-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [check]]
            [schema.contrib.number :refer [gt lt between]]
            [schema.contrib.human :refer [human-explain]]))

(deftest human-test-unit
  (testing "lt"
    (is (= "42 is not a java.lang.String"
           (human-explain (check s/Str 42)))))

  (testing "either"
    (is (= "1 fails all of the following:-
  it is not a java.lang.String
  it is not gt 42
"
           (human-explain (check (s/either s/Str
                                           (gt 42))
                                 1))))))
