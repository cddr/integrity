(ns integrity.human-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [check]]
            [integrity.number :refer [gt lt between]]
            [integrity.human :refer [human-explain]]))

(deftest human-test-unit
  (testing "Int"
    (is (= "yolo is not integer"
           (human-explain (check s/Int "yolo")))))

  (testing "Keyword"
    (is (= "yolo is not keyword"
           (human-explain (check s/Keyword "yolo")))))

  (testing "lt"
    (is (= "42 is not a java.lang.String"
           (human-explain (check s/Str 42)))))

  (testing "eq"
    (is (= "1 is not eq with 42"
           (human-explain (check (s/eq 42) 1)))))

  (testing "pred"
    (is (= "1 is not even"
           (human-explain (check (s/pred #(even? %)
                                         `(even))
                                 1)))))

  (testing "enum"
    (is (= "1 is not one of #{2 3 4}"
           (human-explain (check (s/enum 2 3 4) 1)))))

  (testing "either"
    (is (= "1 fails all of the following:-
  it is not a java.lang.String
  it is not gt 42
"
           (human-explain (check (s/either s/Str
                                           (gt 42))
                                 1)))))

  (testing "nested"
    (is (= {:name "42 is not a java.lang.String"}
           (human-explain (check {:name s/Str} {:name 42}))))
    (is (= {:name "42 is not a java.lang.String"
            :age "yolo is not integer"}
           (human-explain (check {:name s/Str
                                  :age s/Int}
                                 {:name 42 :age "yolo"}))))))
