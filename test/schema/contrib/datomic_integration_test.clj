(ns schema.contrib.datomic-integration-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Num Inst]]
            [datomic.api :as d]
            [schema.contrib.datomic :as db]))

(def test-db-uri "datomic:mem://schema-test")

(defn list-attrs [db]
  (set (map first
            (d/q '[:find ?name :where [_ :db.install/attribute ?a] [?a :db/ident ?name]]
                 db))))

(defn db-with [db transactions]
  (:db-after (d/with db transactions)))

(defn id-gen []
  (d/tempid :db.part/db))

(def Tweet
  {:created-at Inst
   :user Str
   :msg Str})

(deftest schema-test
  (testing "can load a generated schema into datomic"
    (d/create-database test-db-uri)
    (let [c (d/connect test-db-uri)
          db (d/db c)]
      (= #{:created-at :user :msg}
         (clojure.set/difference
          (list-attrs (db-with db (db/attributes Tweet id-gen)))
          (list-attrs db))))))
