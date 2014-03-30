(ns schema.contrib.human
  (:require [schema.core :as s]
            [schema.utils :as utils]))            

(defn human-explain
  ([err]
     (when err
       (human-explain err true)))
  ([err top-level?]
     (let [err* {:explain (utils/.-fail-explanation err)
                 :schema (utils/.-schema err)
                 :value (utils/.-value err)
                 :expectation @(utils/.-expectation-delay err)
                 :fail-explanation (utils/.-fail-explanation err)}
           humanize (fn [v]
                      (if (symbol? v)
                        (str (name v))
                        v))
           show-val (fn []
                      (if top-level?
                        (:value err*)
                        "it"))]
       (cond
        (instance? schema.core.Predicate (:schema err*))
        (with-out-str
          (print (show-val) "is not ")
          (apply print (let [[op & args] (first (:expectation err*))]
                         (conj (interpose "and" (map humanize args))
                               (humanize op)))))

        (instance? schema.core.Either (:schema err*))
        (with-out-str
          (println (show-val) "fails all of the following:-")
          (doseq [exp (map #(human-explain % nil)
                           (map #(s/check % (:value err*))
                                (.-schemas (:schema err*))))]
            (println " " exp)))

        true
        (with-out-str
          (print (show-val) "is not a" (:schema err*)))))))


