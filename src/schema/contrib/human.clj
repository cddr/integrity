(ns schema.contrib.human
  (:require [schema.core :as s]
            [schema.utils :as utils]
            [taoensso.tower :as tower :refer (t *locale*)]))

(def dictionary
  {:dev-mode? true
   :fallback-locale :en
   :dictionary
   {:en {:schema.contrib.human
         {:it           "it"
          :not-eq       "is not eq with"
          :not-one-of   "is not one of"
          :is-not       "is not"
          :and          "and"
          :fails-all    "fails all of the following:-"
          :is-not-a     "is not a"}}}})

(defn- humanize [v]
  (if (symbol? v)
    (str (name v))
    v))

(defn- tval [key]
  (t *locale* dictionary key))

(defn- show-val [err parent]
  (if (nil? parent)
    (:value err)
    (tval ::it)))

(defn- error [check-result]
  {:explain (utils/.-fail-explanation check-result)
   :schema (utils/.-schema check-result)
   :value (utils/.-value check-result)
   :expectation @(utils/.-expectation-delay check-result)
   :fail-explanation (utils/.-fail-explanation check-result)})

(defprotocol ValidationTranslator
  (translate [schema error parent]))

(extend schema.core.EqSchema
  ValidationTranslator
  {:translate (fn [schema e parent]
                (with-out-str
                  (print (show-val e parent) (tval ::not-eq)
                         (second (:expectation e)))))})

(defn human-expectation? [expectation]
  (and (list? expectation)
       (symbol? (first (first expectation)))))

(extend schema.core.EnumSchema
  ValidationTranslator
  {:translate (fn [schema error parent]
                (with-out-str
                  (print (show-val error parent) (tval ::not-one-of) "")
                  (print (.vs (:schema error)))))})

(extend schema.core.Predicate
  ValidationTranslator
  {:translate (fn [schema error parent]
                (with-out-str
                  (print (show-val error parent) (tval ::is-not) "")
                  (apply print (cond
                                (human-expectation? (:expectation error))
                                (let [[op & args] (first (:expectation error))]
                                  (conj (interpose (tval ::and) (map humanize args))
                                       (humanize op)))

                                true
                                (:expectation error)))))})

(extend schema.core.Either
 ValidationTranslator
 {:translate (fn [schema e p]
               (with-out-str
                 (println (show-val e p) (tval ::fails-all))
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
                  (print (show-val error parent) (tval ::is-not-a) (:schema error))))})

(defn human-explain [check-result]
  (when check-result
    (let [error (error check-result)]
      (binding [*locale* (or *locale* :en)]
        (translate (:schema error) error nil)))))
