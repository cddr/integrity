(ns integrity.datomic
  "Maps datomic attribute definitions to prismatic schemata
and vice versa"
  (:require [datomic.api :as d]
            [schema.core :as s :refer [Str Num Inst Int Bool Keyword]]))


(def ^{:private true}
  schema->datomic
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

(defmulti attribute
  "Implementing methods should return a function that will generate a
datomic attribute when given it's id as the one and only argument"
  (fn [ident schema]
    (class schema)))

(defmethod attribute ::leaf [ident schema]
  {:db/id (d/tempid :db.part/db)
   :db/ident ident
   :db/valueType ((comp val find) schema->datomic schema)
   :db/cardinality :db.cardinality/one
   :db.install/_attribute :db.part/db})

(defmethod attribute ::vector [ident schema]
  {:db/id (d/tempid :db.part/db)
   :db/ident ident
   :db/valueType ((comp val find) schema->datomic (first schema))
   :db/cardinality :db.cardinality/many
   :db.install/_attribute :db.part/db})

(defn attributes [schema]
  "Given a prismatic schema, returns a list of datomic attributes"
  (let [mk-attr (fn [acc entry]
                  (conj acc
                        (attribute (key entry) (val entry))))]
    (reduce mk-attr [] schema)))

(derive java.lang.Class                ::leaf)

;; This is only required because some single valued Schema types (e.g. Int)
;; are implemented as predicates. We're not trying to generate datomic attributes
;; for arbitrary predicates
(derive schema.core.Predicate          ::leaf)

(derive clojure.lang.IPersistentVector ::vector)


;; prismatic/schema utils

(def ^{:private true}
  datomic->schema
  {:db.type/string           Str
   :db.type/boolean          Bool
   :db.type/long             Long
   :db.type/double           Number
   :db.type/integer          Int
   :db.type/float            Float
   :db.type/instant          Inst
   :db.type/ref              (s/either Int Keyword)
   })

(defn schema
  ([attributes required]
     (let [build-attr (fn [attr]
                        (let [ident (:db/ident attr)
                              type (val (find datomic->schema (:db/valueType attr)))]
                          (if (contains? required ident)
                            [ident type]
                            [(s/optional-key ident) type])))]
       (let [attrs (filter :db.install/_attribute attributes)]
         (apply hash-map (mapcat build-attr attrs)))))
  ([attributes]
     (schema attributes #{})))
