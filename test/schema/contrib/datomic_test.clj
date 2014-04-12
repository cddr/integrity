(ns schema.contrib.datomic-test
  (:require [clojure.test :refer :all]
            [schema.core :as s :refer [Str Num]]
            [datomic.api :as d]
            [schema.contrib.datomic :as db]
            [clojure.data :as data]))

(defn dattr [name type]
  {:db/ident name
   :db/valueType type
   :db/cardinality :db.cardinality/one
   :db.install/_attribute :db.part/db})

(deftest datomic-test
  (testing "attributes for basic datomic types"
    (let [schema {:a Str
                  :b Num
                  :c Boolean
                  :d Long
                  :e Float}
          dissoc-id #(dissoc %1 :db/id)]
      (is (= (set (map #(dattr (key %) (val %))
                       {:a :db.type/string
                        :b :db.type/double
                        :c :db.type/boolean
                        :d :db.type/long
                        :e :db.type/float
                        }))
             (set (map dissoc-id (db/attributes schema (constantly 42)))))))))

