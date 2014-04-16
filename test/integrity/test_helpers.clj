(ns integrity.test-helpers
  (:require [datomic.api :as d]))

(def ^{:dynamic true}
  *prefix* [])

(defmacro with-prefix [prefix & body]
  `(binding [*prefix* (conj *prefix* ~prefix)]
     (vec (concat ~@body))))

(defn build-ident [ident]
  (if (empty? *prefix*)
    ident
    (let [parts (concat (map (comp str name) (interpose "." *prefix*))
                        "/"
                        (name ident))]
      (keyword (apply str parts)))))

(defn attr [ident type & flags]
  (let [enum-idents (:enum-idents type)]
    ((comp vec concat)
     [{:db/id (d/tempid :db.part/db)
       :db/ident  (build-ident ident)
       :db/valueType (cond
                      (keyword? type) (keyword (str "db.type/" (name type)))
                      (:enum-idents type) :db.type/ref)
       :db/cardinality (keyword (str "db.cardinality/"
                                     (name (or (first (filter #{:many :one} flags))
                                               :one))))
       :db.install/_attribute :db.part/db}]
       enum-idents)))       
