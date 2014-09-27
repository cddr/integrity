(ns integrity.walkers
  (:require [schema.core :as s]))

(defn lookup
  "Returns a function that takes a single object `data`, and returns a new
object of the same shape with any references replaced with the result of
calling `(lookup-ref data)`

To determine whether some part of the data is a reference, it checks the
corresponding section of `input-schema` for equality with `reference-type`"
  [input-schema reference-type lookup-ref]
  (letfn [(walk-fn [s]
            (let [walk (s/walker s)]
              (fn [data]
                (if (= reference-type s)
                  (lookup-ref data)
                  (walk data)))))]
    (s/start-walker walk-fn input-schema)))
