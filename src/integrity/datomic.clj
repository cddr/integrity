(ns integrity.datomic
  "Maps datomic attribute definitions to prismatic schemata
and vice versa"
  (:require [datomic.api :as d]
            [schema.core :as s :refer [Str Num Inst Int Bool Keyword]]
            [clojure.walk :as w]))

(def Ref {:schema (s/either Int Keyword)
          :attr-factory (fn [ident]
                          {:db/id (d/tempid :db.part/db)
                           :db/ident ident
                           :db/valueType :db.type/ref
                           :db/cardinality :db.cardinality/one
                           :db.install/_attribute :db.part/db})})

(def ^{:private true}
  schema->datomic
  {Str                      :db.type/string
   Bool                     :db.type/boolean
   Long                     :db.type/long
   ;java.Math.BigInteger     :db.type/bigint
   Num                      :db.type/double
   Int                      :db.type/long
   Float                    :db.type/float
   Inst                     :db.type/instant
   ;java.Math.BigDecimal     :db.type/bigdec
   })

(defmulti attribute
  "Implementing methods should return a function that will generate a
datomic attribute when given it's id as the one and only argument"
  (fn dispatch
    ([ident schema]
       (dispatch ident schema false))
    ([ident schema unique]
       (class schema))))

(defmethod attribute ::leaf
  ([ident schema]
     (attribute ident schema false))
  ([ident schema unique]
     (let [uniqueness (if unique {:db/unique unique} {})]
       (merge uniqueness
              {:db/id (d/tempid :db.part/db)
               :db/ident ident
               :db/valueType ((comp val find) schema->datomic schema)
               :db/cardinality :db.cardinality/one
               :db.install/_attribute :db.part/db}))))

(defmethod attribute ::vector
  ([ident schema]
     (attribute ident schema false))
  ([ident schema unique]
     (if unique
       (throw (Exception. "attributes with cardinality of many cannot be unique"))
       {:db/id (d/tempid :db.part/db)
        :db/ident ident
        :db/valueType ((comp val find) schema->datomic (first schema))
        :db/cardinality :db.cardinality/many
        :db.install/_attribute :db.part/db})))

(defn attributes
  ([schema]
     (attributes schema {}))
  ([schema uniqueness]
     (let [mk-attr (fn [k v]
                     (attribute k v (k uniqueness)))]
       (reduce (fn [acc [k v]]
                 (if (extends? schema.core/Schema (class v))
                   (into acc [(mk-attr k v)])
                   (into acc (conj (attributes (into {} v) uniqueness)
                                   ((:attr-factory Ref) k)))))
               []
               (seq schema)))))

(derive java.lang.Class                ::leaf)

;; This is only required because some single valued Schema types (e.g. Int)
;; are implemented as predicates. We're not trying to generate datomic attributes
;; for arbitrary predicates
(derive schema.core.Predicate          ::leaf)

(derive clojure.lang.IPersistentVector ::vector)
