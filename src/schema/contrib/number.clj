(ns schema.contrib.number
  (:require [schema.core :as s]
            [schema.utils :as utils]
            [schema.contrib.human :refer :all]))

(defn lt
  "Returns a `schema.core` predicate that passes when it's input is less than `high`"
  [high]
  (s/pred (fn [x]
            (< x high))
          `(lt ~high)))

(defn gt
  "Returns a `schema.core` predicate that passes when it's input is greater than `low`"
  [low]
  (s/pred (fn [x]
            (> x low))
          `(gt ~low)))

(defn between
  "Returns a `schema.core` predicate that passes when it's input is between `low` and `high`"
  [low high]
  (s/pred (fn [x]
            (< low x high))
          `(between ~low ~high)))
          
