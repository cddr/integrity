(ns integrity.datomic-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Bool Num Int Inst]]
            [datomic.api :as d]
            [integrity.datomic :as db]
            [integrity.test-helpers :refer [attr]]))

(deftest test-leaves
  (let [attr (fn [type]
               (db/attribute :yolo type))]
    (are [expected actual] (= expected actual)
         :db.type/string (:db/valueType (attr Str))
         :db.type/boolean (:db/valueType (attr Bool))
         :db.type/double (:db/valueType (attr Num))
         :db.type/integer (:db/valueType (attr Int))
         :db.type/instant (:db/valueType (attr Inst)))))

(deftest test-maps
  (let [map-schema {:name Str
                    :address Str}]
    (is (= [(db/attribute :name Str)
            (db/attribute :address Str)]
           (db/attributes map-schema)))))

(deftest test-prismatic->datomic
  (testing "ident is copied from schema"
    (let [test-attr (db/attribute :yolo Str)]
      (= :foo (:db/ident test-attr))))

  (testing "single valued attributes"
    (let [test-attr (fn [attr]
                      (db/attribute :yolo attr))
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
      (is (multi? (db/attribute :yolo [Str]))))))


(deftest test-datomic->prismatic
  (let [attrs (concat (attr :a :string)
                      (attr :b :boolean)
                      (attr :c :float))]
    (testing "datomic validator"
      (is (= {(s/optional-key :a) Str
              (s/optional-key :b) Bool
              (s/optional-key :c) Float}
             (db/schema attrs))))

    (testing "datomic validator with required keys"
      (is (= {:a Str
              :b Bool
              (s/optional-key :c) Float}
             (db/schema attrs #{:a :b}))))))
