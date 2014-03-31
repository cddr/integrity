(ns schema.contrib.human
  (:require [schema.core :as s]
            [schema.utils :as utils]))

(defn- humanize [v]
  (if (symbol? v)
    (str (name v))
    v))

(defn- show-val [err parent]
  (if (nil? parent)
    (:value err)
    "it"))

(defn- error [check-result]
  {:explain (utils/.-fail-explanation check-result)
   :schema (utils/.-schema check-result)
   :value (utils/.-value check-result)
   :expectation @(utils/.-expectation-delay check-result)
   :fail-explanation (utils/.-fail-explanation check-result)})

(defprotocol ValidationTranslator
  (translate [schema error parent]))

(extend schema.core.Predicate
  ValidationTranslator
  {:translate (fn [schema error parent]
                (with-out-str
                  (print (show-val error parent) "is not ")
                  (apply print (let [[op & args] (first (:expectation error))]
                                 (conj (interpose "and" (map humanize args))
                                       (humanize op))))))})

(extend schema.core.Either
 ValidationTranslator
 {:translate (fn [schema e p]
               (with-out-str
                 (println (show-val e p) "fails all of the following:-")
                 (doseq [exp (map (fn [sub-schema]
                                    (let [val (:value e)
                                          check-result (s/check sub-schema val)]
                                      (if check-result
                                        (translate (:schema (error check-result))
                                                   (error check-result)
                                                   e))))
                                  (.-schemas schema))]
                   (println " " exp))))})



(extend java.lang.Class
  ValidationTranslator
  {:translate (fn [schema error parent]
                (with-out-str
                  (print (show-val error parent) "is not a" (:schema error))))})

(defn human-explain [check-result]
  (when check-result
    (let [error (error check-result)]
      (translate (:schema error) error nil))))

