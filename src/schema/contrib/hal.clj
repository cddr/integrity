(ns schema.contrib.hal
  (:require [schema.core :as s :refer [Str Any Bool Keyword]]))

(def ^{:private true}
  opt s/optional-key)

(def Link
  "Returns a schema that matches a HAL Link Object

See [ietf spec](http://tools.ietf.org/html/draft-kelly-json-hal-06#section-5)"
  {:href Str
   (opt :templated)   Bool
   (opt :type)        Str
   (opt :deprecation) Str
   (opt :name)        Str
   (opt :profile)     Str
   (opt :title)       Str
   (opt :hreflang)    Str})

(def Resource
  "Returns a schema that matches a HAL Resource Object

See [ietf spec](http://tools.ietf.org/html/draft-kelly-json-hal-06#section-4)"
  {:_links
   {:self Link
    Keyword (s/either Link [Link])}
   Keyword Any})


(def Curie
  "Returns a schema that matches a HAL Curie Object

See [ietf spec](http://tools.ietf.org/html/draft-kelly-json-hal-06#section-8.2)"
  {:name Str
   :href Str
   (opt :templated) Bool})

