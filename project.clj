(defproject cddr/integrity "0.3.0-SNAPSHOT"
  :description "A collection of libraries for maintaining data integrity"
  :url "https://github.com/cddr/integrity"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/schema "0.4.4"]
                 [com.taoensso/tower "2.0.2"]
                 [com.damballa/abracad "0.4.12"]]
  :plugins [[codox "0.6.7"]]
  :codox {:writer codox-md.writer/write-docs
          :output-dir "doc/v0.3.0"
          :src-dir-uri "http://github.com/cddr/integrity/blob/master/"}


  :profiles
  {:test {:dependencies [[com.datomic/datomic-free "0.9.4899"]]
          :resource-paths ["test/resources"]}
   :dev {:dependencies [[com.datomic/datomic-free "0.9.4899"]]
         :resource-paths ["test/resources"]}})
