function assert_exists {
  if [ -s "$1" ]; then
    return 0
  else
    echo "tooling: Expected file $1 to exist"
  fi
}

function test_generate_docs {
    echo "  test generate docs"
    lein doc
    if ( assert_exists doc/index.html &&
            assert_exists doc/schema.contrib.human.html &&
            assert_exists doc/schema.contrib.number.html &&
            assert_exists doc/schema.contrib.hal.html ); then
        exit 0
    else
        echo "Failed to generate documentation"
        rm -rf doc
        exit 1
    fi
    rm -rf doc
}

echo
echo "test tooling"
test_generate_docs


