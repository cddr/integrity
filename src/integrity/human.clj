;; The schema `check` function returns an object which is not really suitable
;; for displaying an error to an end-user. However, it usually contains enough
;; information to generate one. This library parses the return value of an invocation
;; of `check` and generates human readable error messages

(ns integrity.human
  (:require [schema.core :as s]
            [schema.spec.core :as spec]
            [schema.utils :as utils]
            [taoensso.tower :as tower :refer [*locale*]])
  (:import (schema.utils ValidationError)))

;; ### Support for internationalization

(def ^{:private true}
  dictionary
  "`dictionary` defines translations so that error messages in multiple
languages can be easily supported"
  {:dev-mode? true
   :fallback-locale :en
   :dictionary
   {:en {:integrity.human
         {:it           "it"
          :not-eq       "is not eq with"
          :not-one-of   "is not one of"
          :is           "is"
          :is-not       "is not"
          :and          "and"
          :fails-all    "fails all of the following:-"
          :is-not-a     "is not a"}}}})

(defn- tval [k]
  (tower/t (or *locale* :en) dictionary k))

;; Helpers
(defn- humanize
  "`humanize` takes a value and returns a human readable representation
of that value"
 [v]
  (if (symbol? v)
    (clojure.string/replace (str (name v)) "?" "")
    v))

(defprotocol HumanExplain
  (human-explain [schema error] "Explain an error related to this schema"))

(defn human-walker
  [input-schema]
  (spec/run-checker
   (fn [s params]
     (let [walk (spec/checker (s/spec s) params)]
       (fn [x]
         (let [result (walk x)]
           (println "walked result: " s result)
           (human-explain s result)))))
   true
   input-schema))

(extend-protocol HumanExplain
  java.lang.Class
  (human-explain [schema result]
    (if (utils/error? result)
      (with-out-str
        (print (.-value (utils/error-val result))
               (tval ::is-not-a)
               schema)))))

(extend-protocol HumanExplain
  schema.core.Predicate
  (human-explain [schema result]
    (if (utils/error? result)
      (let [err (utils/error-val result)
            [pred val] @(.-expectation-delay err)]
        (with-out-str
          (print (.-value err) (tval ::is) (or (.-fail-explanation err) 'not)
                 (let [name (.-pred-name schema)]
                   (cond
                    (symbol? name) (humanize name)
                    (seq name) (str (humanize (first name))
                                    " "
                                    (apply str
                                           (interpose (str " " (tval ::and) " ")
                                                      (map humanize (rest name)))))))))))))

(extend-protocol HumanExplain
  schema.core.EqSchema
  (human-explain [schema result]
    (if (utils/error? result)
      (let [err (utils/error-val result)]
        (with-out-str
          (print (.-value err) (tval ::not-eq) (.-v schema)))))))

(extend-protocol HumanExplain
  schema.core.EnumSchema
  (human-explain [schema result]
    (if (utils/error? result)
      (let [err (utils/error-val result)]
        (with-out-str
          (print (.-value err) (tval ::not-one-of)
                 (.-vs schema)))))))

(extend-protocol HumanExplain
  schema.core.MapEntry
  (human-explain [schema result]
    (println "explaining map entry result: " (str schema) result)
    (if-let [err (second result)]
      (do
        (println {(:key-schema schema) err})
        {(:key-schema schema) err}))))

(into {} [(seq {1 "foo"})
          (seq {2 "foo"})
          [nil "foo"]])

(extend-protocol HumanExplain
  clojure.lang.PersistentArrayMap
  (human-explain [schema result]
    (println "explaining persistent array map result: " schema result)
    (let [m (into {} (let [foo (map (fn [[s value]]
                                      (println "explaining: " s value)
                                      (seq (human-explain (s/map-entry s ((:key-schema s) schema))
                                                          value)))
                                    result)]
                       (println "foo: " foo)
                       foo))]
      (if (empty? m)
        nil
        m))))

(extend-protocol HumanExplain
  schema.core.Either
  (human-explain [schema result]
    result))
