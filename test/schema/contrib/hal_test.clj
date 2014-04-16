(ns integrity.hal-test
  (:require [clojure.test :refer :all]
            [integrity.hal :as hal]
            [schema.core :as s :refer [check]]))

(deftest hal-resource-tests
  (let [self (fn [other-links]
               {:_links
                (conj
                 {:self {:href "/foo"}}
                 other-links)})]
    (testing "resource"
      (testing "should have link to self"
        (is (nil? (check hal/Resource (self nil)))))

      (testing "should permit related links"
        (is (nil? (check
                   hal/Resource
                   (self {:child {:href "/foo/child"}})))))

      (testing "should permit arbitrary properties"
        (is (nil? (check
                   hal/Resource
                   (merge (self nil)
                          {:bar "baz"
                           :a-list [1 2 3]
                           :an-object {:prop "yolo"}}))))))))

(deftest hal-link-tests
  (testing "link"
    (is (nil? (check hal/Link {:href "http://example.com"})))
    (is (nil? (check hal/Link {:href "http://example.com"
                               :templated true
                               :type "application/hal+json"
                               :deprecation "http://example/deprected-apis"
                               :name "cool api 2.0"
                               :profile "http://example/profile"
                               :title "A cool resource"
                               :hreflang "en"})))
    (is (check hal/Link "yolo"))
    (is (check hal/Link {:unknown "attr"}))))

(deftest hal-curie-tests
  (testing "curie"
    (is (nil? (check hal/Curie {:name "doc"
                                :href "/doc/{rel}"
                                :templated true})))
    (is (check hal/Curie "yolo"))))

(deftest hal-test-unit
  (hal-resource-tests)
  (hal-link-tests)
  (hal-curie-tests))
