# Changelog

## Unreleased Changes

 * Add basic HAL types

Add schema vars for the basic HAL types of Resource, Link, and
Curie. See the [HAL Specification][hal] for full details

[hal]: http://tools.ietf.org/html/draft-kelly-json-hal-06
 * Add support for generating API documentation

To generate project docs using codox, run

    $ lein doc

On pushes to the github repo, travis will attempt to generate the docs
and the tests will fail if [test/tooling.sh]
(https://github.com/cddr/schema.contrib/blob/master/test/tooling.sh)
returns a non-zero exit code
 * Add test for generating documentation
 * Fix README formatting
 * Make the changelog a little prettier
 * Add note about contributing in README
 * Add script to automatically maintain Changelog.md
 * Refactor to human-explain to support easier internationalization
 * Fix typo in README docs
 * Add travis build badge
 * Use travis for CI instead of shippable
 * Fix lein install command
 * Install lein at before_install
 * Add shippable build file
 * Add translator for schema.core.Enum
 * Add note in README about pred schemas needing to be named for human translation
 * Add test for pred translator; bail out on translating preds without a name
 * Add translator for schema.core.EqSchema
 * Fix description and url in project.clj
 * Refactor human-explain to use ValidationTranslator protocol
 * Initial Commit
