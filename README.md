# integrity

![The Schema Toolbox](resources/icon.png)

The [Schema Project](https://github.com/Prismatic/schema) is aimed at
providing a method of defining the "shape" of your public data structures.
This library is a collection of Schema utilities that we believe may be
useful to a large part of the community.

## Status

 * ![Build Status](https://travis-ci.org/cddr/integrity.svg)

## Usage

Add the latest version into your dependencies

```
(defproject
  :dependencies [[cddr/integrity "0.2.0-SNAPSHOT"]])
```

### integrity.hal

HAL is the [hypertext application language](http://stateless.co/hal_specification.html).
As the summary at the link above describes, HAL based APIs are easily discoverable by
client applications. The vars in this namespace may be helpful when generating
walkers that require knowledge of HAL data-structures. For example usage, see
the tests

[HAL Tests](https://github.com/cddr/integrity/blob/master/test/integrity/hal_test.clj)

### integrity.human

When some data input is checked against some schema, `prismatic/schema`
returns a ValidationError object. The `human-explain` function translates
this error object into a message that should be surfaceable to an end-user.
For example usage, see the tests

[Human Explain Tests](https://github.com/cddr/integrity/blob/master/test/integrity/human_test.clj)

When using schema's `pred` type constructor, be sure to give your predicate
a name which satisfies the function `human-expectation?`. This should ensure
that the information needed by `ValidationTransformer` to print a human
readable message is attached to your predicate function.

### integrity.number

Schema defines a `pred` utility which builds a schema that matches it's
input if the supplied predicate returns true. Here, we use this to build
numeric schemas that can be more specific than just a type of number.

For example `(gt 21)` builds a schema one could use to ensure the input
data is old enough to buy booze. For more examples, see the tests

[Number Tests](https://github.com/cddr/integrity/blob/master/test/integrity/number_test.clj)

### integrity.walkers

Schema walker generators take a schema as input, and use it to return a
function that walks input data in-step with the the corresponding schema. For
example, the `lookup` walker replaces "references" in the input document
with the result of looking them up in an external data source. For example
usage, see the tests

[Walker Tests](https://github.com/cddr/integrity/blob/master/test/integrity/walkers_test.clj)

## Contributing

If you've found a bug, you have the following options, ordered by usefulness
to the community

1. Issue a pull request that contains a test that fails against a released
   version, together with a change that fixes the test.
2. Create an issue describing the minimal steps to reproduce; the version
   of the project you are using; what you see; and what you expected to see
3. Create an issue describing the problem in as much detail as you can

If you have an idea for a feature, make a github issue and lets talk about it

### Running the tests

Before issuing a pull request, please use the following command to ensure
the unit tests pass and the API docs can be generated
```
$ lein do clean, test
```

## Credits

[Tree Icon](https://www.iconfinder.com/icons/60170/content_tree_icon#size=256) designed by
[Custom Icon Design](http://www.customicondesign.com) from iconfinder is licensed for
non-commercial use
    
## License

Copyright © 2014 Andy Chambers

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
