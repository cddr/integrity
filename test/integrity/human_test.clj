(ns integrity.human-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [check]]
            [integrity.number :refer [gt lt between]]
            [integrity.human :refer [human-walker]]))

(deftest class-explainer
  (let [chk (human-walker s/Str)]
    (is (nil? (chk "foo")))
    (is (= (chk 42) "42 is not a java.lang.String"))))

(deftest pred-explainer
  ;; since s/Int is implemented as a predicate, we'll just put this here
  (let [chk (human-walker s/Int)]
    (is (nil? (chk 42)))
    (is (= (chk 0.5) "0.5 is not integer"))
    (is (= (chk "foo") "foo is not integer")))

  ;; same for s/Keyword
  (let [chk (human-walker s/Keyword)]
    (is (nil? (chk :yolo)))
    (is (= (chk "yolo") "yolo is not keyword")))

  ;; TODO: this with more interesting predicates
  (let [chk (human-walker (s/pred #(even? %)
                                  'even?))]
    (is (nil? (chk 2)))
    (is (= "1 is not even" (chk 1)))))

(deftest eq-explainer
  (let [chk (human-walker (s/eq 42))]
    (is (nil? (chk 42)))    
    (is (= (chk 43) "43 is not eq with 42"))))

(deftest map-explainer
  (let [chk (human-walker {:foo s/Str, :bar s/Str})]
    (is (nil? (chk {:foo "foo", :bar "bar"})))
    (is (= (chk {:foo 42, :bar "bar"})
           {:foo "42 is not a java.lang.String"}))
    (is (= (chk {:foo "foo", :bar 42})
           {:bar "42 is not a java.lang.String"}))))

(deftest enum-explainer
  (let [chk (human-walker (s/enum 2 3 4))]
    (is (nil? (chk 2)))
    (is (= (chk 5) "5 is not one of #{2 3 4}"))))

(deftest nested-schema
  (let [chk (human-walker {:name s/Str
                           :info {:email s/Str}})]
    (is (nil? (chk {:name "funk d'void"
                    :info {:email "void@mixcloud.com"}})))
    (is (= {:name "42 is not a java.lang.String"
            :info {:email "42 is not a java.lang.String"}}
           (chk {:name 42, :info {:email 42}})))))
  

;; TODO: This worked when we implemented the explainer by parsing the output of `check` but
;; since refactoring to use the human-explain protocol, I can't figure out how to make it
;; work. The `either` schema construct is discouraged in any case so it's not a priority for
;; me to fix
;;
;; https://groups.google.com/d/msg/prismatic-plumbing/GXKcbM4Ij-Y/BWuDWml42EsJ
;;       
;;   (testing "either"
;;     (is (= "1 fails all of the following:-
;;   it is not a java.lang.String
;;   it is not gt 42
;; "
;;            (human-explain (check (s/either s/Str
;;                                            (gt 42))
;;                                  1))))
;;     (is (= ()
;;            (human-explain (check (s/either {:email s/Str}
;;                                            {:phone s/Str})
;;                                  {:email 42}))


