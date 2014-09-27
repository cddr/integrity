(ns integrity.datomic-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Bool Num Int Inst Keyword]]
            [datomic.api :as d]
            [integrity.datomic :as db]
            [integrity.test-helpers :refer [attr]])
  (:import [java.util Date UUID]))

(def AllTypes
  {:a Str
   :b Bool
   :c Num
   :d Int
   :e Inst
   :f {:nested Bool}})

(defn with-test-db [schema uniqueness f]
  (let [uri "datomic:mem://test-db"]
    (d/create-database uri)
    (let [c (d/connect uri)
          db (:db-after (d/with (d/db c) (db/attributes schema uniqueness)))
          result (f db)]
      (d/release c)
      (d/delete-database uri)
      result)))

(defn find-attr [db name]
  (let [q '[:find ?id
            :in $ ?name
            :where [_ :db.install/attribute ?id]
                   [?id :db/ident ?name]]]
    (d/entity db (ffirst (d/q q db name)))))
  
(deftest test-schema
  (with-test-db AllTypes {}
    (let [type= (fn [db t attr-name]
                  (= t (:db/valueType (find-attr db attr-name))))]
      (fn [db]
        (is (type= db :db.type/string :a))
        (is (type= db :db.type/boolean :b))
        (is (type= db :db.type/double :c))
        (is (type= db :db.type/long :d))
        (is (type= db :db.type/instant :e))
        (is (type= db :db.type/ref :f))

        (is (type= db :db.type/boolean :nested))))))
  
(deftest test-unique-constraints
  (with-test-db AllTypes {:a :db.unique/identity
                          :b :db.unique/value}
    (fn [db]
      (is (= :db.unique/identity (:db/unique (find-attr db :a))))
      (is (= :db.unique/value (:db/unique (find-attr db :b)))))))

;; (deftest test-simple-map
;;   (let [map-schema {:name Str
;;                     :address Str}
;;         uniqueness {:name :db.unique/identity}]
;;     (is (= (set (remove-ids [(db/attribute :name Str :db.unique/identity)
;;                              (db/attribute :address Str)]))
;;            (set (remove-ids (db/attributes map-schema uniqueness)))))))

;; (deftest test-nested-map
;;   (let [schema {:name Str
;;                 :address {:street Str
;;                           :city Str}}]
;;     (is (= (set (remove-ids [(db/attribute :name Str)
;;                              (db/attribute :street Str)
;;                              (db/attribute :city Str)
;;                              ((:attr-factory db/Ref) :address)]))
;;            (set (remove-ids (db/attributes schema)))))))

;; (def test-datomic-facts
;;   (let [project-data {:name "cddr/integrity"
;;                       :url "https://github.com/cddr/integrity"
;;                       :license {:name "Eclipse Public License"
;;                                 :url "http://www.eclipse.org/legal/epl-v10.html"}}]
;;     (db/datomic-facts 0 project-data))

