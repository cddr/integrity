(ns integrity.avro-test
  (:require
   [abracad.avro :as avro]
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [schema.core :as s :refer [Str Bool Num Int Inst Keyword]]
   [integrity.avro :refer :all]
   [integrity.test-helpers :refer [attr]])
  (:import [java.util Date UUID]))

(def test-schema (->map (avro/parse-schema
                         (slurp (io/reader
                                 (io/resource "avro-test-schema.json"))))))

(defn- good? [attr values]
  (not-any? #(attr (s/check test-schema %))
            (map (partial hash-map attr) values)))

(defn- bad? [attr values]
  (every? #(attr (s/check test-schema %))
          (map (partial hash-map attr) values)))

(deftest test-primitive-types
  (testing "boolean"
    (is (good? :boolean [true false]))
    (is (bad? :boolean [42 "42" {}])))

  (testing "int"
    (is (good? :int (map int [0 1 Integer/MAX_VALUE Integer/MIN_VALUE])))
    (is (bad? :int [0.0 (+ Integer/MAX_VALUE 1)])))

  (testing "long"
    (is (good? :long (map long [0 1 Long/MAX_VALUE Long/MIN_VALUE])))
    (is (bad? :long [(+ (bigdec Long/MAX_VALUE) 1)
                     (- (bigdec Long/MIN_VALUE) 1)])))

  (testing "float"
    (is (good? :float (map float [3.14])))
    (is (bad? :float (map int [3.14]))))

  (testing "double"
    (is (good? :double [3.14]))
    (is (bad? :double (map int [3.14]))))

  (testing "bytes"
    (is (good? :bytes [(.getBytes "hello")]))
    (is (bad? :bytes ["hello"])))

  (testing "string"
    (is (good? :string ["abc"]))
    (is (bad? :string [42]))))

(deftest test-complex-types
  (testing "record"
    (is (good? :record [{:a "a", :b "b"}]))
    (is (bad? :record [{:a "missing b"}]))
    (is (bad? :record ["not a record"])))

  (testing "enum"
    (is (good? :enum ["a" "b" "c"]))
    (is (bad? :enum ["d"])))

  (testing "array"
    (is (good? :array [["first" "second"]]))
    (is (bad? :array [[1 2]])))

  (testing "map"
    (is (good? :map [{"one" 1, "two" 2}]))
    (is (bad? :map [{"one" 1.0, "two" 2.0}])))

  (testing "fixed"
    (is (good? :fixed ["1234123412341234"]))
    (is (bad? :fixed ["1" "123412341234123"]))))
