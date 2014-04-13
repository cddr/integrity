(ns schema.contrib.datomic-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Bool Num Int Inst]]
            [datomic.api :as d]
            [schema.contrib.datomic :as db]
            [clojure.data :as data]))

(deftest datomic-test
  (testing "ident is copied from schema"
    (let [test-attr ((db/attribute :yolo Str) 42)]
      (= :foo (:db/ident test-attr))))

  (testing "single valued attributes"
    (let [test-attr (fn [attr]
                      ((db/attribute :yolo attr) 42))
          val-type (fn [schema-type]
                     (:db/valueType (test-attr schema-type)))]
      (is (= :db.type/string (val-type Str)))
      (is (= :db.type/boolean (val-type Bool)))
      (is (= :db.type/double (val-type Num)))
      (is (= :db.type/integer (val-type Int)))
      (is (= :db.type/instant (val-type Inst)))))

  (testing "multi-valued attributes"
    (let [multi? (fn [attr]
                   (= :db.cardinality/many (:db/cardinality attr)))]
      (is (multi? ((db/attribute :yolo [Str]) 42))))))
