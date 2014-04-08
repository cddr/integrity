# schema.contrib

The schema contributitons library is a collection of namespaces, each
of which implements features that we believe may be useful to a large
part of the `prismatic/schema` community.

## Status

 * ![Build Status](https://travis-ci.org/cddr/schema.contrib.svg)

## Usage

Add the latest version into your dependencies

```
(defproject
  :dependencies [[cddr/schema.contrib "0.1.0"]])
```

### schema.contrib.hal

HAL is the [hypertext application language](http://stateless.co/hal_specification.html)
As summary at the link above describes, HAL based APIs are easily discoverable by
client applications. The vars in this namespace may be helpful when generating
walkers that require knowledge of HAL data-structures. For example usage, see
the tests

[HAL Tests](https://github.com/cddr/schema.contrib/blob/next/test/schema/contrib/hal_test.clj)

### schema.contrib.walkers

Schema walker generators take a schema as input, and use it to return a
function that walks input data in-step with the the corresponding schema. For
example, the `lookup` walker replaces "references" in the input document
with the result of looking them up in an external data source. For example
usage, see the tests

[Walker Tests](https://github.com/cddr/schema.contrib/blob/next/test/schema/contrib/walkers_test.clj)

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

## Contributing

If you've found a bug, you have the following options, ordered by usefulness
to the community

1. Issue a pull request that contains a test that fails against a released
   version, together with a change that fixes the test.
2. Create an issue describing the minimal steps to reproduce; the version
   of the project you are using; what you see; and what you expected to see
3. Create an issue describing the problem in as much detail as you can

If you have an idea for a feature, make github issue and lets talk about it

### Developer Tooling

Please consider adding this [pre-commit hook](https://gist.github.com/cddr/9906472)
to your local repo to ensure the Changelog is kept up-to-date

### Running the tests

Before issuing a pull request, please use the following command to ensure
the unit tests pass and the API docs can be generated
```
$ lein do clean, test && bash test/tooling.sh
```
    
## License

Copyright © 2014 Andy Chambers

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
