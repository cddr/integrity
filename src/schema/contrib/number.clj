(ns schema.contrib.number
  (:require [schema.core :as s]
            [schema.utils :as utils]))

(defn lt [high]
  (s/pred (fn [x]
            (< x high))
          `(lt ~high)))

(defn gt [low]
  (s/pred (fn [x]
            (> x low))
          `(gt ~low)))

(defn between [low high]
  (s/pred (fn [x]
            (< low x high))
          `(between ~low ~high)))
          
