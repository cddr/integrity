(ns integrity.datomic-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Bool Num Int Inst]]
            [datomic.api :as d]
            [integrity.datomic :as db]
            [integrity.test-helpers :refer [attr]]))

(def remove-ids (fn [v] (map #(dissoc % :db/id) v)))

(deftest test-leaves
  (let [gen-attr (fn
                   ([type]
                      (db/attribute :yolo type))
                   ([type unique]
                      (db/attribute :yolo type unique)))]
    (are [expected actual] (= expected actual)
         :db.type/string (:db/valueType (gen-attr Str))
         :db.type/boolean (:db/valueType (gen-attr Bool))
         :db.type/double (:db/valueType (gen-attr Num))
         :db.type/integer (:db/valueType (gen-attr Int))
         :db.type/instant (:db/valueType (gen-attr Inst))
         :db.type/ref (:db/valueType ((:attr-factory db/Ref) :yolo)))
    (let [many-ints (gen-attr [Int])]
      (is (= :db.type/integer (:db/valueType many-ints)))
      (is (= :db.cardinality/many (:db/cardinality many-ints))))

    (is (= :db.unique/identity (:db/unique (gen-attr Str :db.unique/identity))))
    (is (= :db.unique/value (:db/unique (gen-attr Str :db.unique/value))))
    (is (thrown-with-msg? Exception #"attributes with cardinality of many cannot be unique"
                          (gen-attr [Str] :db.unique/identity)))))

(deftest test-simple-map
  (let [map-schema {:name Str
                    :address Str}
        uniqueness {:name :db.unique/identity}]
    (is (= (set (remove-ids [(db/attribute :name Str :db.unique/identity)
                             (db/attribute :address Str)]))
           (set (remove-ids (db/attributes map-schema uniqueness)))))))

(deftest test-nested-map
  (let [schema {:name Str
                :address {:street Str
                          :city Str}}]
    (is (= (set (remove-ids [(db/attribute :name Str)
                             (db/attribute :street Str)
                             (db/attribute :city Str)
                             ((:attr-factory db/Ref) :address)]))
           (set (remove-ids (db/attributes schema)))))))

