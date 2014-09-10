(ns integrity.datomic-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Bool Num Int Inst]]
            [datomic.api :as d]
            [integrity.datomic :as db]
            [integrity.test-helpers :refer [attr]]))

(def remove-ids (fn [v] (map #(dissoc % :db/id) v)))

(deftest test-leaves
  (let [attr (fn [type]
               (db/attribute :yolo type))]
    (are [expected actual] (= expected actual)
         :db.type/string (:db/valueType (attr Str))
         :db.type/boolean (:db/valueType (attr Bool))
         :db.type/double (:db/valueType (attr Num))
         :db.type/integer (:db/valueType (attr Int))
         :db.type/instant (:db/valueType (attr Inst))
         :db.type/ref (:db/valueType ((:attr-factory db/Ref) :yolo)))
    (let [many-ints (attr [Int])]
      (is (= :db.type/integer (:db/valueType many-ints)))
      (is (= :db.cardinality/many (:db/cardinality many-ints))))))

(deftest test-simple-map
  (let [map-schema {:name Str
                    :address Str}]
    (is (= (set (remove-ids [(db/attribute :name Str)
                             (db/attribute :address Str)]))
           (set (remove-ids (db/attributes map-schema)))))))

(deftest test-nested-map
  (let [schema {:name Str
                :address {:street Str
                          :city Str}}]
    (is (= (set (remove-ids [(db/attribute :name Str)
                             (db/attribute :street Str)
                             (db/attribute :city Str)
                             ((:attr-factory db/Ref) :address)]))
           (set (remove-ids (db/attributes schema)))))))

