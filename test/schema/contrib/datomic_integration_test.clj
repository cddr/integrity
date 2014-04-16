(ns integrity.datomic-integration-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Num Inst Int]]
            [datomic.api :as d]
            [integrity.datomic :as db]))

(def test-db-uri "datomic:mem://schema-test")

(defn list-attrs [db]
  (set (map first
            (d/q '[:find ?name :where [_ :db.install/attribute ?a] [?a :db/ident ?name]]
                 db))))

(defn db-with [transactions]
  (d/create-database test-db-uri)
  (let [c (d/connect test-db-uri)]
    (:db-after (d/with (d/db c) transactions))))

(defn id-gen []
  (d/tempid :db.part/db))

;; datomic helpers

(defn installed-attrs [db]
  (let [q '[:find ?name
            :where [_ :db.install/attribute ?a]
            [?a :db/ident ?name]]]
    (map (comp #(d/entity db %) first) (d/q q db))))

(defn find-attr [db name]
  (let [q '[:find ?id
            :in $ ?name
            :where [_ :db.install/attribute ?id]
                   [?id :db/ident ?name]]]
    (d/entity db (ffirst (d/q q db name)))))

(def Tweet
  {:created-at Inst
   :user Str
   :msg Str})

(deftest schema-test
  (testing "can load a generated schema into datomic"
    (= #{:created-at :user :msg}
       (clojure.set/difference
        (list-attrs (db-with (db/attributes Tweet id-gen)))
        (list-attrs (db-with nil))))))
