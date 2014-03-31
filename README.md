# schema.contrib

The schema contributitons library is a collection of namespaces, each
of which implements features that we believe may be useful to a large
part of the `prismatic/schema` community.

## Status

 * ![Build Status](https://travis-ci.org/cddr/schema.contrib.svg)

## Usage

### schema.contrib.number

`schema.contrib.number` provides helpers for building number validators

    (ns my.app
      (:require [schema.contrib.number :as num]))
    
    (def schema
      {:rev-share (num/between 0 1)
       :age (num/gt 21)
       :income (num/lt 100000)})

### schema.contrib.human

`schema.contrib.human` provides functions that produce errors intended
for end-users

    (ns my.app
      (:require [schema.contrib.human :as hum]
                [schema.contrib.number :as num]
                [schema.core :as s]))
    
    (defn show-errors [input-val]
      (human-explain (s/check
                       {:name s/Str
                        :age (num/gt 21)})
        input-val))
    
    (show-errors {:name "foo" :age 25})
    =>nil
    
    (show-errors {:name 42 :age 25})
    42 is not a valid java.lang.String
    =>nil
    
    (show-errors {:name "Billy the kid" :age 18})
    18 is not gt 21
    =>nil
    
    (defn show-composite-errors [input-val]
      (human-explain (s/check
                       {:yolo (s/either
                                s/Str
                                (num/lt 42))})
        input-val))
    
    (show-errors {:yolo 100})
    100 fails all of the following:-
      it is not a java.lang.String
      it is not lt 42
    =>nil

When using schema's `pred` type constructor, be sure to give your predicate
a name which satisfies the function `human-expectation?`. This should ensure
that the information needed by `ValidationTransformer` to print a human
readable message is attached to your predicate function.
    
## License

Copyright © 2014 Andy Chambers

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
