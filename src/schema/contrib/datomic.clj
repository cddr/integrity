(ns schema.contrib.datomic
  (:require
   [schema.core :as s :refer [Str Num Inst Int Bool]]))


(def schema->datomic
  {Str                      :db.type/string
   Bool                     :db.type/boolean
   Long                     :db.type/long
   ;java.Math.BigInteger     :db.type/bigint
   Num                      :db.type/double
   Int                      :db.type/integer
   Float                    :db.type/float
   Inst                     :db.type/instant
   ;java.Math.BigDecimal     :db.type/bigdec
   })

(defmulti attribute (fn [ident schema]
                      (class schema)))

(defmethod attribute ::leaf [ident schema]
  (fn [id]
    {:db/id id
     :db/ident ident
     :db/valueType ((comp val find) schema->datomic schema)
     :db/cardinality :db.cardinality/one
     :db.install/_attribute :db.part/db}))

(defmethod attribute ::vector [ident schema]
  (fn [id]
    {:db/id id
     :db/ident ident
     :db/valueType ((comp val find) schema->datomic (first schema))
     :db/cardinality :db.cardinality/many
     :db.install/_attribute :db.part/db}))

(defn attributes [schema id-fn]
  (let [mk-attr (fn [acc entry]
                  (conj acc
                        ((attribute (key entry) (val entry)) (id-fn))))]
    (reduce mk-attr [] schema)))

(derive java.lang.Class                ::leaf)

;; This is only required because some single valued Schema types (e.g. Int)
;; are implemented as predicates. We're not trying to generate datomic attributes
;; for arbitrary predicates
(derive schema.core.Predicate          ::leaf)

(derive clojure.lang.IPersistentVector ::vector)
