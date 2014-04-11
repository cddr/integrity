(ns schema.contrib.datomic
  (:require
   [schema.core :as s :refer [Str Num Inst Int]]))


(def schema->datomic
  {Str                      :db.type/string
   Boolean                  :db.type/boolean
   Long                     :db.type/long
;   java.Math.BigInteger      :db.type/bigint
   Num                      :db.type/double
   Int                      :db.type/integer
   Float                    :db.type/float
   Inst                     :db.type/instant
;   java.Math.BigDecimal      :db.type/bigdec}
   })

(defn attributes [schema id-fn]
  (let [mk-attr (fn [acc entry]
                  (conj acc
                        {:db/id (id-fn)
                         :db/ident (key entry)
                         :db/valueType (val (find schema->datomic (val entry)))
                         :db/cardinality :db.cardinality/one
                         :db.install/_attribute :db.part/db}))]
    (reduce mk-attr [] schema)))
