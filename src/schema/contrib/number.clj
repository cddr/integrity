(ns integrity.number
  (:require [schema.core :as s]
            [schema.utils :as utils]
            [integrity.human :refer :all]))

(defn lt
  "Returns a `schema.core` predicate that passes when it's input is less than `high`"
  [high]
  (s/pred (fn [x]
            (< x high))
          `(lt ~high)))

(defn lte
  "Returns a `schema.core` predicate that passes when it's input is less than
or equal to `high`"
  [high]
  (s/pred (fn [x]
            (<= x high))
          `(lte ~high)))

(defn gt
  "Returns a `schema.core` predicate that passes when it's input is greater than `low`"
  [low]
  (s/pred (fn [x]
            (> x low))
          `(gt ~low)))

(defn gte
  "Returns a `schema.core` predicate that passes when it's input is greater than
or equal to `low`"
  [low]
  (s/pred (fn [x]
            (>= x low))
          `(gte ~low)))

(defn between
  "Returns a `schema.core` predicate that passes when it's input is between `low` and `high`"
  [low high]
  (s/pred (fn [x]
            (< low x high))
          `(between ~low ~high)))
          
