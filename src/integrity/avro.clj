(ns integrity.avro
  "Generates a prismatic schema from an avro one"
  (:require
   [clojure.java.io :as io]
   [schema.core :as s :refer [Bool Str]]
   [abracad.avro :as avro])
  (:import [org.apache.avro Schema$Type]))

(def ByteArray (Class/forName "[B"))

(defn ->map [avro-schema]
  (condp = (.getType avro-schema)

    ;; Primitive types
    Schema$Type/BOOLEAN Bool
    Schema$Type/INT     Integer
    Schema$Type/LONG    Long
    Schema$Type/FLOAT   Float
    Schema$Type/DOUBLE  Double
    Schema$Type/BYTES   ByteArray
    Schema$Type/STRING  Str

    ;; Complex Types
    Schema$Type/RECORD
    (apply hash-map (mapcat (fn [key val]
                              [(keyword key) val])
                            (map #(.name %) (.getFields avro-schema))
                            (map #(->map (.schema %)) (.getFields avro-schema))))

    Schema$Type/ENUM
    (apply s/enum (.getEnumSymbols avro-schema))

    Schema$Type/ARRAY
    [(->map (.getElementType avro-schema))]

    Schema$Type/MAP
    {Str (->map (.getValueType avro-schema))}

    Schema$Type/FIXED
    (s/pred (fn [str-val]
              (<= (.getFixedSize avro-schema) (count str-val)))
            'exceeds-fixed-size)))

(defn avro-errors [schema value]
  (let [schema-as-map (->map schema)]
    (s/check schema-as-map value)))


