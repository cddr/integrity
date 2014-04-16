(defproject cddr/integrity "0.2.0-SNAPSHOT"
  :description "A collection of libraries for maintaining data integrity"
  :url "https://github.com/cddr/integrity"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/schema "0.2.1"]
                 [com.taoensso/tower "2.0.2"]

                 ;; TODO: figure out how to put this in dev-dependencies
                 [codox-md "0.2.0"]]
  :plugins [[codox "0.6.7"]]
  :codox {:writer codox-md.writer/write-docs
          :output-dir "doc/v0.2.0"
          :src-dir-uri "http://github.com/cddr/integrity/blob/master/"}


  :profiles
  {:test {:dependencies [[com.datomic/datomic-free "0.9.4707"]]}
   :dev {:dependencies [[com.datomic/datomic-free "0.9.4707"]]}})
