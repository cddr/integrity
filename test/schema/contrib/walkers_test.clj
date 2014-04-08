(ns schema.contrib.walkers-test
  (:require [clojure.test :refer :all]
            [schema.contrib.walkers :refer [lookup]]
            [schema.core :as s :refer [Str]]))

(deftest walkers-resolve-test
  (testing "can resolve a datom"
    (let [ticker? {:ticker Str}
          ticker (fn [str] {:ticker str})
          stocks {(ticker "AAPL") "apple"
                  (ticker "GOOG") "google"
                  (ticker "TWTR") "twitter"}
          schema [ticker?]]
      (is (= ["apple" "twitter" "google"]
             ((lookup schema ticker? #(second (find stocks %)))
              [{:ticker "AAPL"}
               {:ticker "TWTR"}
               {:ticker "GOOG"}]))))))
