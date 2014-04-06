(defproject schema.contrib "0.1.0"
  :description "A collection of namespaces that operate on prismatic/schema
metadata and functions"
  :url "https://github.com/cddr/schema.contrib"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/schema "0.2.1"]
                 [com.taoensso/tower "2.0.2"]

                 ;; TODO: figure out how to put this in dev-dependencies
                 [codox-md "0.2.0"]]
  :plugins [[codox "0.6.7"]]
  :codox {:writer codox-md.writer/write-docs
          :output-dir "doc/v0.1.0"
          :src-dir-uri "http://github.com/cddr/schema.contrib/blob/v0.1.0/"})
